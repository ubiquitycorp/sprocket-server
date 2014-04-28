package com.ubiquity.api.interceptors;

import java.io.IOException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.ubiquity.api.dto.model.ErrorDto;

/***
 * 
 * Intercepts any IOException thrown by an endpoint and returns 
 * the the 503 status code and JSON payload
 * 
 * @author chris
 *
 */
@Provider
public class IOExceptionMapper implements ExceptionMapper<IOException> {

	private Logger log = LoggerFactory.getLogger(getClass());

	/***
	 * Returns error response and sets the response code
	 */
	public Response toResponse(IOException e) {
		log.error("[ERROR]", e);
		
		ErrorDto response = new ErrorDto();
		response.getMessages().add(e.getMessage());
		
		return Response.status(503).entity(new Gson().toJson(response)).build();


	}

}
