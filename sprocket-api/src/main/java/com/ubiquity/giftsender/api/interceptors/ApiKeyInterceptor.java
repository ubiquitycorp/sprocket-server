package com.ubiquity.giftsender.api.interceptors;

import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.AcceptedByMethod;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;

import com.ubiquity.giftsender.api.exception.HttpException;
import com.ubiquity.giftsender.service.ServiceFactory;
import com.ubiquity.identity.service.AuthenticationService;

/***
 * Intercepter triggers when a @Secure annotation is present. It checks the
 * session cache for the passed-in session token
 * 
 * @author chris
 * 
 */
@Provider
@ServerInterceptor
public class ApiKeyInterceptor implements PreProcessInterceptor,
		AcceptedByMethod {

	@Context
	private ServletContext servletContext;

	@SuppressWarnings("rawtypes")
	public boolean accept(Class declaring, Method method) {
		return method.isAnnotationPresent(Secure.class);
	}

	/***
	 * Combines path parameter and session id header value and checks cache to
	 * see if the token is valid
	 */
	public ServerResponse preProcess(HttpRequest req, ResourceMethod method)
			throws Failure, WebApplicationException {

		// CE: Are the clients sending you "SessionId" headers? The standard
		// for API skeys is the authorization header. 
		List<String> values = req.getHttpHeaders().getRequestHeader("ApiKey");
		if (values != null && !values.isEmpty()) {
			// get token / user id pair
			String apiKey = values.get(0);
			String userId = req.getUri().getPathParameters().getFirst("userId");

			AuthenticationService authenticationService = ServiceFactory.getAuthenticationService();
			// get the user id
			if (authenticationService.isUserAuthenticated(userId, apiKey)) {
				return null; // proceed
			}
		}
		throw new HttpException("Invalid API Key", 401);
	}

}
