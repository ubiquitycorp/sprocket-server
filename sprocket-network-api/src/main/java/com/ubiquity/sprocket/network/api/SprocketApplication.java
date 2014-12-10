package com.ubiquity.sprocket.network.api;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.ubiquity.sprocket.network.api.endpoints.ContentEndPoint;
import com.ubiquity.sprocket.network.api.endpoints.SocialEndPoint;
import com.ubiquity.sprocket.network.api.endpoints.UsersEndpoint;

@ApplicationPath("services")
public class SprocketApplication extends Application {
	
	 private Set<Object> singletons = new HashSet<Object>();

	  public SprocketApplication() {
	    singletons.add(new UsersEndpoint());
	    singletons.add(new SocialEndPoint());
	    singletons.add(new ContentEndPoint());
	  }

	  @Override
	  public Set<Object> getSingletons() {
	    return singletons;
	  }

}