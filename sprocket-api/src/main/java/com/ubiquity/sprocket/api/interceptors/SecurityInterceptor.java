package com.ubiquity.sprocket.api.interceptors;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.api.annotations.Active;
import com.ubiquity.api.annotations.Secure;
import com.ubiquity.api.exception.ErrorKeys;
import com.ubiquity.identity.service.AuthenticationService;
import com.ubiquity.integration.api.exception.AuthorizationException;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.sprocket.domain.ConfigurationRules;
import com.ubiquity.sprocket.service.ClientConfigurationService;
import com.ubiquity.sprocket.service.ServiceFactory;

/***
 * Intercepter triggers when a @Secure annotation is present. It checks the
 * session cache for the passed-in session token
 * 
 * @author chris
 * 
 */
@Provider
@ServerInterceptor
public class SecurityInterceptor implements ContainerRequestFilter {
	private static final String AUTHORIZATION_PROPERTY = "Authorization";
	private static final String AUTHENTICATION_SCHEME = "Basic";
	private Logger log = LoggerFactory.getLogger(getClass());

	/***
	 * Combines path parameter and session id header value and checks cache to
	 * see if the token is valid
	 */
	@Override
	public void filter(ContainerRequestContext requestContext)
			throws IOException {
		ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker) requestContext
				.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");
		Method method = methodInvoker.getMethod();

		if (method.isAnnotationPresent(Active.class)) {
			String externalNetworkPathParam = requestContext
					.getUriInfo().getPathParameters()
					.getFirst("externalNetworkId");
			
			Integer externalNetworkId = null;
			if (externalNetworkPathParam != null)
			{
				externalNetworkId = Integer.parseInt(externalNetworkPathParam);
			}
			
			String userIdPathParam = requestContext
					.getUriInfo().getPathParameters()
					.getFirst("userId");
			Long userId = null;
			
			if (userIdPathParam != null)
			{
				userId = Long.parseLong(userIdPathParam);
			}
			// check if it is allowed to use endpoint
			if (!checkAvailability(externalNetworkId, userId, method))
				throw new UnsupportedOperationException();
		}

		if (method.isAnnotationPresent(Secure.class)
				|| method.isAnnotationPresent(DeveloperSecure.class)) {
			List<String> values = requestContext.getHeaders().get(
					AUTHORIZATION_PROPERTY);
			if (values != null && !values.isEmpty()) {

				String apiKey = values.get(0).replaceFirst(
						AUTHENTICATION_SCHEME + " ", "");
				String userId = "";
				@SuppressWarnings("rawtypes")
				AuthenticationService authenticationService = null;
				if (method.isAnnotationPresent(Secure.class)) {
					authenticationService = ServiceFactory.getUserAuthService();
					userId = requestContext.getUriInfo().getPathParameters()
							.getFirst("userId");
				} else if (method.isAnnotationPresent(DeveloperSecure.class)) {
					authenticationService = ServiceFactory
							.getDeveloperAuthService();
					userId = requestContext.getUriInfo().getPathParameters()
							.getFirst("developerId");
				}
				log.debug("Recieved user_id :" + userId + ",apiKey :" + apiKey);

				// get the user id
				if (!authenticationService.isAuthenticated(userId, apiKey)) {
					throw new AuthorizationException(ServiceFactory
							.getErrorsConfigurationService().getErrorMessage(
									ErrorKeys.AUTHORIZATION_ERROR), null);
				}
			} else
				throw new AuthorizationException(ServiceFactory
						.getErrorsConfigurationService().getErrorMessage(
								ErrorKeys.AUTHORIZATION_ERROR), null);
		}
	}

	/**
	 * This method checks the availability of using endpoints, by checking network and general rules
	 * @param externalNetworkId
	 * @param userId
	 * @param method POST or GET method
	 * @return
	 */
	private Boolean checkAvailability(Integer externalNetworkId, Long userId, Method method) {
		Boolean isEnabled = false;
		ClientConfigurationService service = ServiceFactory
				.getClientConfigurationService();
		String path = method.getAnnotation(Path.class).value();

		ExternalNetwork network = null;
		if (externalNetworkId != null)
		{
			network = ExternalNetwork.getNetworkById(externalNetworkId);
		}
		
		if (method.isAnnotationPresent(GET.class)) {
			if (path.endsWith("messages")) {
				isEnabled = service.getValue(
						ConfigurationRules.messagesEnabled, network);
			} else if (path.endsWith("activities")
					|| path.endsWith("activities/synced")) {
				isEnabled = service.getValue(
						ConfigurationRules.activitiesEnabled, network);
			} else if (path.endsWith("contacts")
					|| path.endsWith("contacts/synced")) {
				isEnabled = service.getValue(
						ConfigurationRules.contactsEnabled, network);
			} else if (path.endsWith("localfeed")) {
				isEnabled = service.getValue(
						ConfigurationRules.localfeedEnabled, network);
			} else if (path.endsWith("videos")) {
				isEnabled = service.getValue(ConfigurationRules.videosEnabled,
						network);
			} else if (path.endsWith("activities/recommended"))
			{
				isEnabled = service.getValue(ConfigurationRules.activitiesRecommended, network);
			} else if (path.endsWith("videos/recommended"))
			{
				isEnabled = service.getValue(ConfigurationRules.videosRecommended, network);
			} else if (path.endsWith("live"))
			{
				isEnabled = service.getValue(ConfigurationRules.searchLiveEnabled,network);
			} else if (path.endsWith("indexed"))
			{
				if (userId == null)
				{
					// There is no bookmarked search if there is no live search
					isEnabled = service.getValue(ConfigurationRules.searchLiveEnabled,network);
				} else if (network != null)
				{
					// checking if searching private data is available
					isEnabled = service.getValue(ConfigurationRules.searchPrivateEnabled,network);
				}
				
			} else if (path.endsWith("favorites") 
					|| path.endsWith("favorites/places/{placeId}") 
					|| path.endsWith("favorites/places/current"))
			{
				isEnabled = service.getValue(ConfigurationRules.favoriteEnabled, network);
			}
			
		} else if (method.isAnnotationPresent(POST.class)) {
			if (path.endsWith("messages")) {
				isEnabled = service.getValue(
						ConfigurationRules.messagesPost, network);
			} else if (path.endsWith("activities")) {
				isEnabled = service.getValue(
						ConfigurationRules.activitiesPost, network);
			} else if (path.endsWith("comment")) {
				isEnabled = service.getValue(
						ConfigurationRules.activitiesCommentContribute, network);
			} else if (path.endsWith("vote")) {
				isEnabled = service.getValue(
						ConfigurationRules.activitiesCommentRateContribute, network);
			}  else if (path.endsWith("activities/engaged")) {
				isEnabled = service.getValue(
						ConfigurationRules.activitiesEngaged, network);
			} else if (path.endsWith("videos/engaged")) {
				isEnabled = service.getValue(
						ConfigurationRules.videosEngaged, network);
			} else if (path.endsWith("live/engaged"))
			{
				isEnabled = service.getValue(ConfigurationRules.searchLiveEnabled, network);
			}
		}
		return isEnabled;
	}
}
