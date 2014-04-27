package com.ubiquity.api.interceptors;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.ubiquity.api.dto.model.ErrorDto;
import com.ubiquity.api.exception.HttpException;

/***
 * 
 * Intercepts any HttpException thrown by an endpoint and returns 
 * the appropriate set status code and JSON payload
 * 
 * @author chris
 *
 */
@Provider
public class HttpExceptionMapper implements ExceptionMapper<HttpException> {

	private Logger log = LoggerFactory.getLogger(getClass());

	/***
	 * Returns error response and sets the response code
	 */
	public Response toResponse(HttpException e) {
		log.error("[ERROR]", e);
		
		ErrorDto response = new ErrorDto();
		response.getMessages().add(e.getMessage());
		
		return Response.status(e.getResponseCode()).entity(new Gson().toJson(response)).build();


	}

}
