package com.ubiquity.sprocket.repository;

import com.niobium.repository.Repository;
import com.niobium.repository.cloud.RemoteAsset;
import com.niobium.repository.mr.MapReduceOutputFile;
import com.ubiquity.sprocket.domain.Profile;


public interface ProfileRepository extends Repository <String, Profile>  {
	
	/***
	 * Produces a report that is ready for upload into a CDN
	 * 
	 * @return
	 */
	MapReduceOutputFile getMostPopularSearchTerms();
}
