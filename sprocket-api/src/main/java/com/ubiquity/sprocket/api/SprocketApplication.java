package com.ubiquity.sprocket.api;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.ubiquity.sprocket.api.endpoints.AdminsEndpoint;
import com.ubiquity.sprocket.api.endpoints.AnalyticsEndpoint;
import com.ubiquity.sprocket.api.endpoints.ClientEndpoint;
import com.ubiquity.sprocket.api.endpoints.ContentEndpoint;
import com.ubiquity.sprocket.api.endpoints.DocumentsEndpoint;
import com.ubiquity.sprocket.api.endpoints.InternalServicesEndpoint;
import com.ubiquity.sprocket.api.endpoints.PlacesEndpoint;
import com.ubiquity.sprocket.api.endpoints.SocialEndpoint;
import com.ubiquity.sprocket.api.endpoints.UsersEndpoint;

@ApplicationPath("services")
public class SprocketApplication extends Application {
	
	 private Set<Object> singletons = new HashSet<Object>();

	  public SprocketApplication() {
		  
	    singletons.add(new DocumentsEndpoint());
	    singletons.add(new SocialEndpoint());
	    singletons.add(new PlacesEndpoint());
	    singletons.add(new UsersEndpoint());
	    singletons.add(new ContentEndpoint());
	    singletons.add(new AnalyticsEndpoint());
	    singletons.add(new ClientEndpoint());
	    singletons.add(new InternalServicesEndpoint());
	    singletons.add(new AdminsEndpoint());

	  }

	  @Override
	  public Set<Object> getSingletons() {
	    return singletons;
	  }

}