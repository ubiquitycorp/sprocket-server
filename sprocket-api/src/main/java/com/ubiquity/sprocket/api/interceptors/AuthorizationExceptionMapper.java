package com.ubiquity.sprocket.api.interceptors;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.ubiquity.api.domain.ServerErrorCode;
import com.ubiquity.api.dto.model.ErrorDto;
import com.ubiquity.social.api.exception.AuthorizationException;

/***
 * 
 * Intercepts any HttpException thrown by an endpoint and returns 
 * the appropriate set status code and JSON payload
 * 
 * @author chris
 *
 */
@Provider
public class AuthorizationExceptionMapper implements ExceptionMapper<AuthorizationException> {

	private Logger log = LoggerFactory.getLogger(getClass());

	/***
	 * Returns error response and sets the response code
	 */
	public Response toResponse(AuthorizationException e) {
		log.error("[ERROR] {}", ExceptionUtils.getRootCauseMessage(e));
		
		ErrorDto response = new ErrorDto();
		response.getMessages().add(e.getMessage());
		if(e.isExternalNetwork)
			response.setCode(ServerErrorCode.ExternalAPI.getCode());
		else
			response.setCode(ServerErrorCode.SprocketAPI.getCode());
		
		return Response.status(Status.UNAUTHORIZED).entity(new Gson().toJson(response)).build();
	}

}
