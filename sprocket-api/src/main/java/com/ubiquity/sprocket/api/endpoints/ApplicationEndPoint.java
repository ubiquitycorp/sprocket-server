package com.ubiquity.sprocket.api.endpoints;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.identity.domain.Application;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.Identity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.service.AuthenticationService;
import com.ubiquity.integration.api.exception.AuthorizationException;
import com.ubiquity.sprocket.api.DtoAssembler;
import com.ubiquity.sprocket.api.dto.model.user.AccountDto;
import com.ubiquity.sprocket.api.dto.model.user.IdentityDto;
import com.ubiquity.sprocket.api.validation.RemoteAuthenticationValidation;
import com.ubiquity.sprocket.api.validation.RemoteRegistrationValidation;
import com.ubiquity.sprocket.service.ServiceFactory;
import com.ubiquity.sprocket.service.SprocketUserAuthService;

@Path("/1.0/application")
public class ApplicationEndPoint {

	private JsonConverter jsonConverter = JsonConverter.getInstance();

	private Logger log = LoggerFactory.getLogger(getClass());

	/***
	 * This method registers user using an external identifier provided by a
	 * developer via sprocket application. The request is authenticated using
	 * appKey and appSecret encoded in base64 in the header
	 * 
	 * @param payload
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/users/registered")
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(InputStream payload,
			@HeaderParam("Authorization") String header) throws IOException {

		Application application = authenticateHeader(header);

		IdentityDto identityDto = jsonConverter.convertFromPayload(payload,
				IdentityDto.class, RemoteRegistrationValidation.class);

		AuthenticationService<User> authenticationService = ServiceFactory
				.getUserAuthService();

		ClientPlatform clientPlatform = ClientPlatform.getEnum(identityDto
				.getClientPlatformId());
		User user = ((SprocketUserAuthService) authenticationService).register(
				identityDto.getIdentifier(), clientPlatform, Boolean.TRUE,
				application);

		// user now has a single, native identity
		String apiKey = AuthenticationService.generateAPIKey();

		// set the account DTO with an api key and new user id and send it back
		AccountDto accountDto = new AccountDto.Builder().apiKey(apiKey)
				.userId(user.getUserId()).build();

		// Save UserId and APIKey in Redis cache database
		authenticationService.saveAuthkey(user.getUserId(), apiKey);
		
		// Save application Id in the Redis 
		ServiceFactory.getUserService().saveApplicationId(user.getUserId(), application.getAppId());
		log.debug("Created user {}", user);

		return Response.ok().entity(jsonConverter.convertToPayload(accountDto))
				.build();
	}

	/***
	 * This method authenticates user by an external identifier provided by a
	 * developer via sprocket application. The request is authenticated using
	 * appKey and appSecret encoded in base64 in the header
	 * 
	 * @param payload
	 * @param header
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("users/authenticated")
	@Produces(MediaType.APPLICATION_JSON)
	public Response authenticate(InputStream payload,
			@HeaderParam("Authorization") String header) throws IOException {

		Application application = authenticateHeader(header);

		IdentityDto identityDto = jsonConverter.convertFromPayload(payload,
				IdentityDto.class, RemoteAuthenticationValidation.class);

		AuthenticationService<User> authenticationService = ServiceFactory
				.getUserAuthService();
		User user = ((SprocketUserAuthService) authenticationService)
				.authenticate(identityDto.getIdentifier(), application);
		if (user == null)
			throw new AuthorizationException("Invalid identifier", null);

		// update user last login
		user.setLastLogin(System.currentTimeMillis());
		ServiceFactory.getUserService().update(user);

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
	 * authenticates header by decoding it and validates appKey and appSecret of
	 * sprocket application
	 * 
	 * @param header
	 * @return
	 */
	private Application authenticateHeader(String header) {
		if (header == null)
			throw new AuthorizationException("Invalid credentials", null);
		
		String[] parts = header.split(" ");
		if(parts.length != 2 || (parts.length == 2 && !parts[0].equals("Basic")))
			throw new AuthorizationException("Invalid authorization header", null);
		String decodedHeader = new String(
				Base64.decodeBase64(parts[1].getBytes()));
		String credentials[] = decodedHeader.split(":");
		if(credentials.length != 2)
			throw new AuthorizationException("Invalid authorization header", null);
		
		log.info(credentials[0] + " " + credentials[1]);
		Application application = ServiceFactory.getDeveloperService()
				.getApplicationByAppkeyAndAppSecret(credentials[0],
						credentials[1]);
		return application;
	}
}
