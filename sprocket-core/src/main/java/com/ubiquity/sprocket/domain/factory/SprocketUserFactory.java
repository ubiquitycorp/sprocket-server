package com.ubiquity.sprocket.domain.factory;

import com.ubiquity.identity.domain.Application;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.domain.factory.UserFactory;
import com.ubiquity.sprocket.domain.SprocketUser;
/***
 * 
 * @author peter.tadros
 *
 */
public class SprocketUserFactory extends UserFactory {

	public static User createUserWithRequiredFieldsUsingApplication(
			String externalIdentifier, ClientPlatform clientPlatform,
			boolean isVerified, Application application) {
		return new SprocketUser.Builder()
				.externalIdentifier(externalIdentifier).createdBy(application)
				.clientPlatform(clientPlatform)
				.createdAt(System.currentTimeMillis())
				.lastUpdated(System.currentTimeMillis())
				.lastLogin(System.currentTimeMillis()).isVerified(isVerified)
				.build();
	}
	
	public static User createUserWithMinimumRequiredFields(String firstName,
			String lastName, String displayName, String email,
			ClientPlatform clientPlatform, boolean isVerified) {
		return new SprocketUser.Builder().displayName(displayName).firstName(firstName)
				.lastName(lastName).clientPlatform(clientPlatform)
				.createdAt(System.currentTimeMillis())
				.lastUpdated(System.currentTimeMillis())
				.lastLogin(System.currentTimeMillis()).email(email)
				.isVerified(isVerified).build();
	}
}
