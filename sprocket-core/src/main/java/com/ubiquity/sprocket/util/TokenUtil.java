package com.ubiquity.sprocket.util;

import java.util.UUID;

public class TokenUtil {

	public static String generateUniqueToken(){
		String token = UUID.randomUUID().toString();
		return token;
	}
}
