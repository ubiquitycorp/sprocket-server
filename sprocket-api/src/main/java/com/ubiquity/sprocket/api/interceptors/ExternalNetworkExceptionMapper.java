package com.ubiquity.sprocket.api.interceptors;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.ubiquity.api.domain.ServerErrorCode;
import com.ubiquity.api.dto.model.ErrorDto;
import com.ubiquity.social.api.exception.ExternalNetworkException;
/***
 * 
 * @author mina.shafik
 *
 */
@Provider
public class ExternalNetworkExceptionMapper implements ExceptionMapper<ExternalNetworkException> {

	private Logger log = LoggerFactory.getLogger(getClass());

	/***
	 * Returns error response and sets the response code
	 */
	public Response toResponse(ExternalNetworkException e) {
		log.error("[ERROR] {}", ExceptionUtils.getRootCauseMessage(e));
		
		ErrorDto response = new ErrorDto();
		response.getMessages().add(e.getMessage());
		response.setCode(ServerErrorCode.ExternalAPI.getCode());
		return Response.status(e.getResponseCode()).entity(new Gson().toJson(response)).build();


	}
}
