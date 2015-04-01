package com.ubiquity.sprocket.network.api.googleplus;

import java.util.UUID;

import com.ubiquity.sprocket.network.api.dto.model.Contact;
import com.ubiquity.sprocket.network.api.googleplus.model.GooglePersonDto;
import com.ubiquity.sprocket.network.api.googleplus.model.RefreshTokenResponseDto;
import com.ubiquity.sprocket.network.api.random.generator.RandomObjectGenerator;

public class GooglePlusMockNetwork {
	
	public static GooglePersonDto Authenticate(Long userId){
		Contact contact = RandomObjectGenerator.generateContact(userId, null);
		return GooglePlusApiDtoAssembler.assembleContact(contact);
	}

	public static RefreshTokenResponseDto refreshToken(String refreshToken) {
		return new RefreshTokenResponseDto.Builder().access_token(refreshToken).expires_in(3600L).scope(UUID.randomUUID().toString()).token_type(UUID.randomUUID().toString()).build();
	}
	
}
