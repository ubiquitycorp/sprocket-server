package com.ubiquity.sprocket.api.interceptors;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.ubiquity.api.dto.model.ErrorDto;

/***
 * 
 * Intercepts an UnSupportedOperationException and throws a 405 method not allowed
 * 
 * @author chris
 *
 */
@Provider
public class UnsupportedOperationExceptionMapper implements ExceptionMapper<UnsupportedOperationException> {

	private Logger log = LoggerFactory.getLogger(getClass());

	/***
	 * Returns error response and sets the response code
	 */
	public Response toResponse(UnsupportedOperationException e) {
		log.error("[ERROR]", e.getMessage(), e);
		
		ErrorDto response = new ErrorDto();
		response.getMessages().add(e.getMessage());
		
		return Response.status(Status.METHOD_NOT_ALLOWED).entity(new Gson().toJson(response)).build();


	}

}
