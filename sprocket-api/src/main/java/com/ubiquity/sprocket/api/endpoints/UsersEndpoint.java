package com.ubiquity.sprocket.api.endpoints;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.common.serialize.JsonConverter;
import com.niobium.repository.cloud.RemoteAsset;
import com.ubiquity.api.exception.HttpException;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.ExternalNetworkApplication;
import com.ubiquity.identity.domain.Identity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.service.AuthenticationService;
import com.ubiquity.integration.api.ContentAPI;
import com.ubiquity.integration.api.ContentAPIFactory;
import com.ubiquity.integration.api.SocialAPI;
import com.ubiquity.integration.api.SocialAPIFactory;
import com.ubiquity.integration.api.exception.AuthorizationException;
import com.ubiquity.integration.api.linkedin.ExchangeService;
import com.ubiquity.integration.api.tumblr.TumblrAPI;
import com.ubiquity.integration.api.twitter.TwitterAPI;
import com.ubiquity.integration.domain.Contact;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.Network;
import com.ubiquity.integration.domain.SocialToken;
import com.ubiquity.media.domain.AudioTrack;
import com.ubiquity.media.domain.Image;
import com.ubiquity.media.domain.Video;
import com.ubiquity.messaging.format.Message;
import com.ubiquity.sprocket.api.DtoAssembler;
import com.ubiquity.sprocket.api.dto.containers.ContactsDto;
import com.ubiquity.sprocket.api.dto.model.ExchangeTokenDto;
import com.ubiquity.sprocket.api.dto.model.SyncDto;
import com.ubiquity.sprocket.api.dto.model.social.ContactDto;
import com.ubiquity.sprocket.api.dto.model.user.AccountDto;
import com.ubiquity.sprocket.api.dto.model.user.IdentityDto;
import com.ubiquity.sprocket.api.dto.model.user.LocationDto;
import com.ubiquity.sprocket.api.dto.model.user.ResetPasswordDto;
import com.ubiquity.sprocket.api.interceptors.Secure;
import com.ubiquity.sprocket.api.validation.ActivationValidation;
import com.ubiquity.sprocket.api.validation.AuthenticationValidation;
import com.ubiquity.sprocket.api.validation.AuthorizationValidation;
import com.ubiquity.sprocket.api.validation.RegistrationValidation;
import com.ubiquity.sprocket.api.validation.ResetValidation;
import com.ubiquity.sprocket.api.validation.UserLocationUpdateValidation;
import com.ubiquity.sprocket.domain.SprocketUser;
import com.ubiquity.sprocket.messaging.MessageConverterFactory;
import com.ubiquity.sprocket.messaging.MessageQueueFactory;
//import com.ubiquity.sprocket.messaging.definition.EventTracked;
import com.ubiquity.sprocket.messaging.definition.ExternalIdentityActivated;
import com.ubiquity.sprocket.messaging.definition.LocationUpdated;
import com.ubiquity.sprocket.service.ServiceFactory;

@Path("/1.0/users")
public class UsersEndpoint {

	private Logger log = LoggerFactory.getLogger(getClass());

	private JsonConverter jsonConverter = JsonConverter.getInstance();

	@GET
	@Path("/ping")
	@Produces(MediaType.APPLICATION_JSON)
	public Response ping() {
		return Response.ok().entity("{\"message\":\"pong\"}").build();
	}

	/***
	 * This method authenticates user's linkedin via native login. Thereafter
	 * users can authenticate
	 * 
	 * @param cookie
	 *            file
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/{userId}/authenticatedlinkedin")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response authenticatedlinkedin(@PathParam("userId") Long userId,
			@CookieParam("linkedin_oauth_77fa6kjljumj8x") String cookie)
			throws Exception {

		// load user
		User user = ServiceFactory.getUserService().getUserById(userId);

		String cookieString = java.net.URLDecoder.decode(cookie, "UTF-8");
		ExchangeService exchangservice = new ExchangeService();
		String[] accesstokens = exchangservice.exchangeToken(cookieString);

		if (accesstokens[0] == null || accesstokens[0].equalsIgnoreCase(""))
			throw new AuthorizationException(
					"Autontication Failed no oAuth_token_returned", null);
		//
		ExternalNetworkApplication externalNetworkApplication = ServiceFactory.getApplicationService()
				.getExAppByExternalNetworkAndClientPlatform(
						user.getCreatedBy(), ExternalNetwork.LinkedIn.ordinal(), ClientPlatform.WEB);
		// create the identity if it does not exist; or use the existing one
		List<ExternalIdentity> identities = ServiceFactory
				.getExternalIdentityService().createOrUpdateExternalIdentity(
						user, accesstokens[0], accesstokens[1], null,
						ClientPlatform.WEB, ExternalNetwork.LinkedIn, null,
						true, externalNetworkApplication);

		ExternalIdentity identity = identities.get(0);
		IdentityDto result = new IdentityDto.Builder()
				.identifier(identity.getIdentifier())
				.clientPlatformId(identity.getClientPlatform().ordinal())
				.build();
		// now send the message activated message to cache invalidate
		sendActivatedMessage(user, identities, result.getClientPlatformId());

		try {

			Contact contact = ServiceFactory.getContactService()
					.getBySocialIdentityId(user.getUserId(),
							identity.getIdentityId());
			ContactDto contactDto = DtoAssembler.assemble(contact);
			return Response.ok()
					.entity(jsonConverter.convertToPayload(contactDto)).build();
		} catch (NoResultException ex) {

			return Response.ok().entity(jsonConverter.convertToPayload(result))
					.build();
		}

	}

	/***
	 * This method used to request Token
	 * 
	 * @param cookie
	 *            InputStream
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/{userId}/requesttoken")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response requesttoken(@PathParam("userId") Long userId,
			InputStream payload) throws Exception {

		// load user
		ServiceFactory.getUserService().getUserById(userId);
		IdentityDto identityDto = jsonConverter.convertFromPayload(payload,
				IdentityDto.class, AuthorizationValidation.class);
		// ClientPlatform clientPlatform = ClientPlatform.getEnum(identityDto
		// .getClientPlatformId());
		ExternalNetwork externalNetwork = ExternalNetwork
				.getNetworkById(identityDto.getExternalNetworkId());
		User user = ServiceFactory.getUserService().getUserById(userId);
		SocialToken requestToken = null;
		if (externalNetwork == ExternalNetwork.Twitter) {
			SocialAPI socialApi = SocialAPIFactory
					.createProviderWithCallBackUrl(externalNetwork,
							identityDto.getRedirectUrl(), user.getCreatedBy());
			TwitterAPI twitterApi = (TwitterAPI) socialApi;
			requestToken = twitterApi.requesttoken();
		} else if (externalNetwork == ExternalNetwork.Tumblr) {
			SocialAPI socialApi = SocialAPIFactory
					.createProviderWithCallBackUrl(externalNetwork,
							identityDto.getRedirectUrl(), user.getCreatedBy());
			TumblrAPI tumblrApi = (TumblrAPI) socialApi;
			requestToken = tumblrApi.requesttoken();
		} else {
			throw new NotImplementedException(
					"ExternalNetwork is not supported");
		}
		if (requestToken == null
				|| requestToken.getAccessToken().equalsIgnoreCase(""))
			throw new HttpException(
					"Autontication Failed no oAuth_token_returned", 401);
		else
			return Response
					.ok()
					.entity("{\"oauthToken\":\""
							+ requestToken.getAccessToken()
							+ "\",\"oauthTokenSecret\":\""
							+ requestToken.getSecretToken() + "\"}")
					// .entity(jsonConverter.convertToPayload(requestToken))
					.build();

	}

	/***
	 * This method authenticates user via native login. Thereafter users can
	 * authenticate
	 * 
	 * @param accessToken
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/authenticated")
	@Produces(MediaType.APPLICATION_JSON)
	public Response authenticate(InputStream payload) throws IOException {
		IdentityDto identityDto = jsonConverter.convertFromPayload(payload,
				IdentityDto.class, AuthenticationValidation.class);

		AuthenticationService<User> authenticationService = ServiceFactory
				.getUserAuthService();
		User user = authenticationService.authenticate(
				identityDto.getUsername(), identityDto.getPassword());
		if (user == null)
			throw new AuthorizationException("Username / password incorrect",
					null);

		// update user last login
		user.setLastLogin(System.currentTimeMillis());
		ServiceFactory.getUserService().update(user);
		// create api key and pass back associated identities for this user (in
		// case of a login from a different device)

		String apiKey = authenticationService.generateAPIKeyIfNotExsits(user
				.getUserId());
		AccountDto accountDto = new AccountDto.Builder().apiKey(apiKey)
				.userId(user.getUserId()).build();

		for (Identity identity : user.getIdentities()) {
			if (identity instanceof ExternalIdentity && identity.getIsActive()) {
				ExternalIdentity externalIdentity = (ExternalIdentity) identity;
				IdentityDto associatedIdentityDto = DtoAssembler
						.assemble(externalIdentity);
				accountDto.getIdentities().add(associatedIdentityDto);
			}
		}

		// Save UserId and APIKey in Redis cache database
		authenticationService.saveAuthkey(user.getUserId(), apiKey);

		log.debug("Authenticated user {}", user);

		return Response.ok().entity(jsonConverter.convertToPayload(accountDto))
				.build();
	}

	/***
	 * This method registers user via native login. Thereafter users can
	 * authenticate
	 * 
	 * @param accessToken
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/registered")
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(InputStream payload) throws IOException {
		IdentityDto identityDto = jsonConverter.convertFromPayload(payload,
				IdentityDto.class, RegistrationValidation.class);

		AuthenticationService<User> authenticationService = ServiceFactory
				.getUserAuthService();

		ClientPlatform clientPlatform = ClientPlatform.getEnum(identityDto
				.getClientPlatformId());
		User user = authenticationService.register(identityDto.getUsername(),
				identityDto.getPassword(), "", "",
				identityDto.getDisplayName(), identityDto.getEmail(),
				clientPlatform, Boolean.TRUE);

		// user now has a single, native identity
		String apiKey = AuthenticationService.generateAPIKey();

		// set the account DTO with an api key and new user id and send it back
		AccountDto accountDto = new AccountDto.Builder().apiKey(apiKey)
				.userId(user.getUserId()).build();

		// Save UserId and APIKey in Redis cache database
		authenticationService.saveAuthkey(user.getUserId(), apiKey);

		log.debug("Created user {}", user);

		return Response.ok().entity(jsonConverter.convertToPayload(accountDto))
				.build();
	}

	/***
	 * Returns only active identities owned by the given user
	 * 
	 * @param userId
	 * @return
	 * @throws IOException
	 */
	@GET
	@Path("/{userId}/identities")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response getIdentities(@PathParam("userId") Long userId)
			throws IOException {

		// load user
		List<Contact> contacts = ServiceFactory.getContactService()
				.findAllContactByUserIdentities(userId);

		ContactsDto contactsDto = new ContactsDto();
		for (Contact contact : contacts) {
			contactsDto.getContacts().add(DtoAssembler.assemble(contact));
		}
		return Response.ok()
				.entity(jsonConverter.convertToPayload(contactsDto)).build();
	}

	@POST
	@Path("/{userId}/identities")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response activate(@PathParam("userId") Long userId,
			InputStream payload) throws IOException {

		// convert payload
		IdentityDto identityDto = jsonConverter.convertFromPayload(payload,
				IdentityDto.class, ActivationValidation.class);

		ClientPlatform clientPlatform = ClientPlatform.getEnum(identityDto
				.getClientPlatformId());
		ExternalNetwork externalNetwork = ExternalNetwork
				.getNetworkById(identityDto.getExternalNetworkId());

		// load user
		User user = ServiceFactory.getUserService().getUserById(userId);

		//log.info("identifier = " + ((SprocketUser)user).getExternalIdentifier());
		// Load External Application
		ExternalNetworkApplication externalNetworkApplication = ServiceFactory.getApplicationService()
				.getExAppByExternalNetworkAndClientPlatform(
						user.getCreatedBy(), externalNetwork.ordinal(),
						clientPlatform);
		// create the identity if it does not exist; or use the existing one
		List<ExternalIdentity> identities = ServiceFactory
				.getExternalIdentityService().createOrUpdateExternalIdentity(
						user, identityDto.getAccessToken(),
						identityDto.getSecretToken(),
						identityDto.getRefreshToken(), clientPlatform,
						externalNetwork, identityDto.getExpiresIn(), true,
						externalNetworkApplication);

		// now send the message activated message to cache invalidate
		sendActivatedMessage(user, identities, identityDto.getClientPlatformId());

		// send off to analytics tracker
		// sendEventTrackedMessage(user, identity);
		try {

			Contact contact = ServiceFactory.getContactService()
					.getBySocialIdentityId(userId,
							identities.get(0).getIdentityId());
			ContactDto contactDto = DtoAssembler.assemble(contact);
			return Response.ok()
					.entity(jsonConverter.convertToPayload(contactDto)).build();
		} catch (NoResultException ex) {
			IdentityDto result = DtoAssembler.assemble(identities.get(0));
			return Response.ok().entity(jsonConverter.convertToPayload(result))
					.build();
		}
	}

	/***
	 * This end point authorizes user in the given content network and retrieves
	 * access token. Then it saves an external identity for this user in given
	 * content network
	 * 
	 * @param userId
	 * @param payload
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/{userId}/authorized")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response authorize(@PathParam("userId") Long userId,
			InputStream payload) throws IOException {

		// convert payload
		IdentityDto identityDto = jsonConverter.convertFromPayload(payload,
				IdentityDto.class, AuthorizationValidation.class);
		ClientPlatform clientPlatform = ClientPlatform.getEnum(identityDto
				.getClientPlatformId());
		ExternalNetwork externalNetwork = ExternalNetwork
				.getNetworkById(identityDto.getExternalNetworkId());
		List<ExternalIdentity> identities = null;

		// load user
		User user = ServiceFactory.getUserService().getUserById(userId);
		// load External Network application 
		ExternalNetworkApplication externalNetworkApplication = ServiceFactory.getApplicationService()
				.getExAppByExternalNetworkAndClientPlatform(
						user.getCreatedBy(), externalNetwork.ordinal(),
						clientPlatform);

		if (externalNetwork.network == Network.Content) {

			ContentAPI contentApi = ContentAPIFactory.createProvider(
					externalNetwork, clientPlatform, user.getCreatedBy());
			String accessToken = contentApi.getAccessToken(
					identityDto.getCode(), identityDto.getRedirectUrl());

			// create the identity if it does not exist; or use the existing one
			identities = ServiceFactory.getExternalIdentityService()
					.createOrUpdateExternalIdentity(user, accessToken,
							identityDto.getSecretToken(),
							identityDto.getRefreshToken(), clientPlatform,
							externalNetwork, null, true,
							externalNetworkApplication);
		} else if (externalNetwork.network == Network.Social) {
			SocialAPI socialApi = SocialAPIFactory.createProvider(
					externalNetwork, clientPlatform, user.getCreatedBy());
			String redirectUri = identityDto.getRedirectUrl();
			if ((externalNetwork.equals(ExternalNetwork.Google) || externalNetwork
					.equals(ExternalNetwork.YouTube))
					&& clientPlatform.equals(ClientPlatform.WEB)) {
				redirectUri = "postmessage";
			}
			// the expiredAt value in externalIdentity object returned from
			// getAccessToken() is equal to expiresIn value
			ExternalIdentity externalidentity = socialApi.getAccessToken(
					identityDto.getCode(), identityDto.getOauthToken(),
					identityDto.getOauthTokenSecret(), redirectUri);

			identities = ServiceFactory.getExternalIdentityService()
					.createOrUpdateExternalIdentity(user,
							externalidentity.getAccessToken(),
							externalidentity.getSecretToken(),
							externalidentity.getRefreshToken(), clientPlatform,
							externalNetwork, externalidentity.getExpiredAt(),
							true, externalNetworkApplication);

		}

		// now send the message activated message to cache invalidate
		sendActivatedMessage(user, identities,
				identityDto.getClientPlatformId());

		// send off to analytics tracker
		// sendEventTrackedMessage(user, identity);

		try {
			Contact contact = ServiceFactory.getContactService()
					.getBySocialIdentityId(userId,
							identities.get(0).getIdentityId());
			ContactDto contactDto = DtoAssembler.assemble(contact);
			return Response.ok()
					.entity(jsonConverter.convertToPayload(contactDto)).build();
		} catch (NoResultException ex) {
			IdentityDto result = new IdentityDto.Builder().identifier(
					identities.get(0).getIdentifier()).build();
			return Response.ok().entity(jsonConverter.convertToPayload(result))
					.build();
		}

	}

	/***
	 * This method exchanges short-lived access token with long-lived one in a
	 * given provider.
	 * 
	 * @param userId
	 * @param externalNetworkId
	 * @param accessToken
	 * @return
	 */
	@POST
	@Path("/{userId}/exchangedToken")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response exchangeAccessToken(@PathParam("userId") Long userId,
			InputStream payload) {

		ExchangeTokenDto exchangeTokenDto = jsonConverter.convertFromPayload(
				payload, ExchangeTokenDto.class);
		ExternalNetwork externalNetwork = ExternalNetwork
				.getNetworkById(exchangeTokenDto.getExternalNetworkId());

		// load user
		User user = ServiceFactory.getUserService().getUserById(userId);
		SocialAPI socialApi = SocialAPIFactory.createProvider(externalNetwork,
				ClientPlatform.WEB, user.getCreatedBy());
		SocialToken token = socialApi.exchangeAccessToken(exchangeTokenDto
				.getAccessToken());

		return Response.ok()
				.entity("{\"accessToken\":\"" + token.getAccessToken() + "\"}")
				.build();
	}

	/***
	 * 
	 * @param email
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/authenticated/reset/requests")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	// @ValidateRequest
	public Response sendResetEmail(InputStream payload) throws IOException {
		IdentityDto identityDto = jsonConverter.convertFromPayload(payload,
				IdentityDto.class, ResetValidation.class);
		ServiceFactory.getUserService().sendResetPasswordEmail(
				identityDto.getUsername());
		return Response.ok().build();
	}

	/***
	 * 
	 * @param payload
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/authenticated/reset/responses")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response resetPassword(InputStream payload) throws IOException {
		ResetPasswordDto resetPasswordDto = jsonConverter.convertFromPayload(
				payload, ResetPasswordDto.class);
		ServiceFactory.getUserService().resetPassword(
				resetPasswordDto.getToken(), resetPasswordDto.getPassword());
		return Response.ok().build();
	}

	/***
	 * This method receives user's location and saves it into database
	 * 
	 * @param payload
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/{userId}/location")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response setLocation(@PathParam("userId") Long userId,
			InputStream payload) throws IOException {

		LocationDto locationDto = jsonConverter.convertFromPayload(payload,
				LocationDto.class, UserLocationUpdateValidation.class);

		sendLocationMessage(userId, locationDto);

		return Response.ok().build();
	}

	/***
	 * This method receives user's location and saves it into database
	 * 
	 * @param payload
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/{userId}/uploaded")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response uploadFile(@PathParam("userId") Long userId,
			@Context HttpServletRequest request) throws IOException {
		String fileName = "";
		RemoteAsset media = null;
		// 50 MB size of memory
		int maxMemorySize = ServiceFactory.getUserService().getMaxMemorySize();
		// 500 MB size of file
		long maxRequestSize = ServiceFactory.getUserService().getMaxFileSize();

		String tempDir = ServiceFactory.getUserService().getFileRepository();
		File tempDirectory = new File(tempDir);
		// Create a factory for disk-based file items
		// DiskFileItemFactory factory = new
		// DiskFileItemFactory(maxMemorySize, tempDirectory);;
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// Set factory constraints

		factory.setSizeThreshold(maxMemorySize); // Set the size threshold,
													// which content will be
													// stored on memory.
		factory.setRepository(tempDirectory); // set the temporary directory
												// to store the uploaded
												// files of size above
												// threshold.

		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(maxRequestSize);
		List<FileItem> items = null;
		long startTime = System.currentTimeMillis();
		try {
			log.debug("Starting parsing file");
			items = upload.parseRequest(request);
			log.debug("Ending parsing file");
		} catch (SizeLimitExceededException e) {
			log.debug("File size exceeded the maximum limit");
			throw new IllegalArgumentException(
					"File size exceeded the maximum limit");
		} catch (FileUploadException e) {
			log.debug("Failed to Upload File");
			throw new RuntimeException("Failed to Upload File");
		}

		if (items != null) {
			Iterator<FileItem> iter = items.iterator();
			while (iter.hasNext()) {
				FileItem item = iter.next();
				if (!item.isFormField() && item.getSize() > 0) {
					String fileExtension = item.getContentType().substring(
							item.getContentType().lastIndexOf("/") + 1);

					fileName = new StringBuilder().append("sprocket")
							.append("_").append(System.currentTimeMillis())
							.append(".").append(fileExtension).toString();

					log.debug(fileName);

					if (item.getContentType().contains("image")) {
						media = new Image.Builder().itemKey(fileName)
								.contentLength(item.getSize()).build();
					} else if (item.getContentType().contains("audio")) {
						media = new AudioTrack.Builder().itemKey(fileName)
								.contentLength(item.getSize()).build();
					} else if (item.getContentType().contains("video")) {
						media = new Video.Builder().itemKey(fileName)
								.contentLength(item.getSize()).build();
					} else
						throw new IllegalArgumentException(
								"Unsupported media type");

					media.setInputStream(item.getInputStream());
					ServiceFactory.getMediaService().create(media);
					long endTime = System.currentTimeMillis();

					log.info("media uploaded successfully:\n url:"
							+ media.getUrl() + "\n upload time: "
							+ (endTime - startTime) / 1000 + " seconds");

				}
			}

			String output = "{\"url\":\"" + media.getUrl() + "\"}";

			return Response.status(200).entity(output).build();
		} else
			throw new IllegalArgumentException("No file found");

	}

	/***
	 * This end point forces synchronization for specific external network
	 * 
	 * @param userId
	 * @param payload
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/{userId}/synced")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response sync(@PathParam("userId") Long userId, InputStream payload)
			throws IOException {

		// convert payload
		SyncDto syncDto = jsonConverter.convertFromPayload(payload,
				SyncDto.class);

		ClientPlatform clientPlatform = ClientPlatform.getEnum(syncDto
				.getClientPlatformId());
		ExternalNetwork externalNetwork = ExternalNetwork
				.getNetworkById(syncDto.getExternalNetworkId());

		ExternalIdentity identity = ServiceFactory.getExternalIdentityService()
				.findExternalIdentity(userId, externalNetwork);

		User user = new User(userId);

		List<ExternalIdentity> identities = new LinkedList<ExternalIdentity>();
		identities.add(identity);
		// now send the message activated message to cache invalidate
		sendActivatedMessage(user, identities, clientPlatform.ordinal());

		return Response.ok().build();

	}

	private void sendActivatedMessage(User user,
			List<ExternalIdentity> identities, Integer clientPlatformId)
			throws IOException {
		for (ExternalIdentity identity : identities) {
			ExternalIdentityActivated content = new ExternalIdentityActivated.Builder()
					.clientPlatformId(clientPlatformId)
					.userId(user.getUserId())
					.identityId(identity.getIdentityId()).build();

			// serialize and send it
			String message = MessageConverterFactory.getMessageConverter()
					.serialize(new Message(content));
			MessageQueueFactory.getCacheInvalidationQueueProducer().write(
					message.getBytes());
		}
	}

	private void sendLocationMessage(Long userId, LocationDto locationDto)
			throws IOException {
		LocationUpdated content = new LocationUpdated.Builder().userId(userId)
				.horizontalAccuracy(locationDto.getHorizontalAccuracy())
				.verticalAccuracy(locationDto.getVerticalAccuracy())
				.timestamp(locationDto.getTimestamp())
				.latitude(locationDto.getLatitude())
				.longitude(locationDto.getLongitude())
				.altitude(locationDto.getAltitude()).build();

		ServiceFactory.getLocationService().addUpdateLocationInCache(userId);
		// serialize and send it
		String message = MessageConverterFactory.getMessageConverter()
				.serialize(new Message(content));

		MessageQueueFactory.getLocationQueueProducer()
				.write(message.getBytes());
		log.debug("message sent: {}", message);
	}

}