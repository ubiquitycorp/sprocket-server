package com.ubiquity.sprocket.api.dto.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ResetPasswordDto {

	@NotNull
	private String token;
	
	@NotNull
	@Size(min = 6, max = 20)
	private String password;

	public String getToken() {
		return token;
	}

	public String getPassword() {
		return password;
	}
}
