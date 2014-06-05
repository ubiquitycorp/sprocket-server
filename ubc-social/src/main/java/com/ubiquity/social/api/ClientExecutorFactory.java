package com.ubiquity.social.api;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public class ClientExecutorFactory {
	
	private static ClientExecutor executor;
	
	static {
		// this initialization only needs to be done once per VM
		RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
	}
	
	public static ClientExecutor createClientExecutor() {
		if(executor == null) {
			ClientConnectionManager cm = new ThreadSafeClientConnManager();
			HttpClient httpClient = new DefaultHttpClient(cm);
			executor = new ApacheHttpClient4Executor(httpClient);
		}
		return executor;
	}

}
