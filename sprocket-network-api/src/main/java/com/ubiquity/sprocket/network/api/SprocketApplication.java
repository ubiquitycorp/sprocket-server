package com.ubiquity.sprocket.network.api;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.ubiquity.sprocket.network.api.endpoints.FacebookEndPoint;
import com.ubiquity.sprocket.network.api.endpoints.GooglePlusEndPoint;
import com.ubiquity.sprocket.network.api.endpoints.YoutubeEndPoint;

@ApplicationPath("services")
public class SprocketApplication extends Application {
	
	 private Set<Object> singletons = new HashSet<Object>();

	  public SprocketApplication() {
	    singletons.add(new YoutubeEndPoint());
	    singletons.add(new FacebookEndPoint());
	    singletons.add(new GooglePlusEndPoint());
	  }

	  @Override
	  public Set<Object> getSingletons() {
	    return singletons;
	  }

}