package com.ubiquity.sprocket.network.api.cache;

import org.apache.commons.configuration.Configuration;

import com.niobium.repository.Cache;
import com.niobium.repository.CacheRedisImpl;

public class CacheFactory {
	private static Cache cache;
		
	public static void initialize(Configuration configuration) {
		cache = new CacheRedisImpl(
				configuration.getInt("redis.user.session.database"));
	}
	
	public static Long getLastRequestTime(Long userId){
		String cachedlastRequest = cache.get(userId.toString());
		if (cachedlastRequest != null)
			return Long.parseLong(cachedlastRequest);
		else
			return null;
	}
	
	public static void setLastRequestTime(Long userId, Long requestTime) {
		cache.put(userId.toString(), requestTime.toString());
	}
	
	public static void checkUpdatedRequestTime(Long userId,Long lastRequestTime, Long thisRequestTime){
		if(thisRequestTime-lastRequestTime > 3600000){
			setLastRequestTime(userId,thisRequestTime);
		}
	}
}
