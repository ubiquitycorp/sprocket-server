package com.ubiquity.giftsender.api.interceptors;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.ubiquity.giftsender.api.dto.model.ErrorDto;

/***
 * 
 * Intercepts any  exception thrown by an endpoint and returns 
 * the appropriate http status code and JSON payload (not the full stack trace).
 * 
 * @author chris
 */
@Provider
public class CatchAllExceptionMapper implements ExceptionMapper<Exception> {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	/***
	 * 	Returns a response with a 500 error code and an error JSON payload containing only 
	 *  the message of the exception (and not the full stack trace)
	 */
	public Response toResponse(Exception e) {
		log.error("[ERROR]", e);
		ErrorDto response = new ErrorDto();
		response.getMessages().add(e.getMessage());
		
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new Gson().toJson(response)).build();
	}
}


