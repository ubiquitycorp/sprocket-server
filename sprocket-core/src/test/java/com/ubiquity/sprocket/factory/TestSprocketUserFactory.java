package com.ubiquity.sprocket.factory;

import java.util.UUID;

import com.ubiquity.identity.domain.Application;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.NativeIdentity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.sprocket.domain.SprocketUser;

public class TestSprocketUserFactory {
	/***
	 * Creates a sprocket user with the minimum required fields set and randomized strings for first name, last name, etc,
	 * and a native identity
	 * 
	 * @return
	 */
	public static User createTestUserWithMinimumRequiredProperties(Application application) {
		User user = new SprocketUser.Builder()
		.createdBy(application)
		.lastUpdated(System.currentTimeMillis())
		.firstName(UUID.randomUUID().toString())
		.lastName(UUID.randomUUID().toString())
		.email(UUID.randomUUID().toString())
		.lastLogin(System.currentTimeMillis())
		.clientPlatform(ClientPlatform.Android)
		.displayName(UUID.randomUUID().toString())
		.canPurchase(false)
		.canRedeem(false)
		.createdAt(System.currentTimeMillis())
		.build();

		NativeIdentity identity = new NativeIdentity.Builder()
		.isActive(Boolean.TRUE)
		.lastUpdated(System.currentTimeMillis())
		.user(user)
		.username(UUID.randomUUID().toString())
		.password(UUID.randomUUID().toString())
		.build();
		user.getIdentities().add(identity);

		return user;
	}
}
