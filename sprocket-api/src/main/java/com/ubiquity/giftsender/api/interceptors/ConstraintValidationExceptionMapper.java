package com.ubiquity.giftsender.api.interceptors;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.niobium.common.exception.ConstraintValidationException;
import com.ubiquity.giftsender.api.dto.model.ErrorDto;

/***
 * 
 * Intercepts any ConstraintValidationException thrown by an endpoint and returns 
 * the appropriate http status code and JSON payload
 * 
 * @author chris
 *
 */
@Provider
public class ConstraintValidationExceptionMapper implements ExceptionMapper<ConstraintValidationException>{

	private Logger log = LoggerFactory.getLogger(getClass());
	
	/***
	 * Returns a response with a 400 error code and error JSON payload
	 */
	public Response toResponse(ConstraintValidationException e) {
		log.error("[ERROR]", e);
		ErrorDto response = new ErrorDto();
		response.getMessages().addAll(e.getMessages());
		
		return Response.status(Response.Status.BAD_REQUEST).entity(new Gson().toJson(response)).build();
	}
}


