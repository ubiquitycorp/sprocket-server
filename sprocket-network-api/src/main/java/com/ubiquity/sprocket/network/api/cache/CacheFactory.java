package com.ubiquity.sprocket.network.api.cache;

import org.apache.commons.configuration.Configuration;

import com.niobium.repository.Cache;
import com.niobium.repository.CacheRedisImpl;

public class CacheFactory {
	private static Cache cache;
	private static Cache accessTokenCache;

	public static void initialize(Configuration configuration) {
		cache = new CacheRedisImpl(
				configuration.getInt("redis.user.session.database"));
		accessTokenCache = new CacheRedisImpl(
				configuration.getInt("redis.access.token.session.database"));
	}

	public static Long getLastRequestTime(Long userId) {
		String cachedlastRequest = cache.get(userId.toString());
		if (cachedlastRequest != null)
			return Long.parseLong(cachedlastRequest);
		else
			return null;
	}

	public static void setLastRequestTime(Long userId, Long requestTime) {
		cache.put(userId.toString(), requestTime.toString());
	}

	public static void checkUpdatedRequestTime(Long userId,
			Long lastRequestTime, Long thisRequestTime) {
		if (thisRequestTime - lastRequestTime > 3600000) {
			setLastRequestTime(userId, thisRequestTime);
		}
	}

	public static synchronized Long findOrCreateUser(String accessToken) {
		String userId = accessTokenCache.get(accessToken);
		if (userId != null) {
			return Long.parseLong(userId);
		} else {
			return createUser(accessToken);
		}
	}

	public static synchronized Long createUser(String accessToken) {
		String userId = accessTokenCache.get("MAXuserID");
		if (userId == null) {
			userId = "10";
		} 
		Long maxUserID = Long.parseLong(userId) +1;
		accessTokenCache.put("MAXuserID", maxUserID.toString());
		accessTokenCache.put(accessToken, maxUserID.toString());
		return maxUserID;
	}
}
