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
 * Intercepts any IllegalArgumenException thrown by an endpoint and returns 
 * the appropriate http status code and JSON payload
 * 
 * @author chris
 *
 */
@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException>{

	private Logger log = LoggerFactory.getLogger(getClass());
	
	/***
	 * Returns a response with a 400 error code and error JSON payload
	 */
	public Response toResponse(IllegalArgumentException e) {
		log.error("[ERROR]", e);
		
		ErrorDto response = new ErrorDto();
		response.getMessages().add(e.getMessage());
		
		return Response.status(Response.Status.BAD_REQUEST).entity(new Gson().toJson(response)).build();
	}
}


