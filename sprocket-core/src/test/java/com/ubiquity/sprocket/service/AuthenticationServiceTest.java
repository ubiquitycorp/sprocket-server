package com.ubiquity.sprocket.service;

import java.util.UUID;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.service.AuthenticationService;
import com.ubiquity.identity.service.UserService;


public class AuthenticationServiceTest {

	private AuthenticationService authenticationService;
	private UserService userService;
	
	private User user;
	
	@Before
	public void setUp() throws Exception {
		Configuration config = new PropertiesConfiguration("test.properties");
		authenticationService = new AuthenticationService(config);
		userService = new UserService(config);

		user = authenticationService.register(UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), ClientPlatform.Android);
	}
	
	@Test
	public void testAuthenticateWithDefaultIdentity() {
		// we should be aable to retrieve this user
		User persisted = userService.getUserById(user.getUserId());
		Assert.assertNotNull(persisted);
	}

}
