package com.ubiquity.api.interceptors;

import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;

@Provider
public class CrossOriginResourceInterceptor implements PostProcessInterceptor {

	@Override
	public void postProcess(ServerResponse response) {
		response.getMetadata().putSingle("Access-Control-Allow-Origin", "*");
        response.getMetadata().putSingle("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        response.getMetadata().putSingle("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, Content-Length");
	}

}
