package com.ubiquity.sprocket.api.dto.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.ubiquity.sprocket.api.validation.EmailAuthenticationValidation;

/***
 * 
 * @author peter.tadros
 *
 */
public class EmailIdentityDto {

	@NotNull(groups = { EmailAuthenticationValidation.class })
	@Size(max = 100, groups = { EmailAuthenticationValidation.class })
	private String username;

	@NotNull(groups = { EmailAuthenticationValidation.class })
	@Size(max = 100, groups = { EmailAuthenticationValidation.class})
	private String password;
	
	@NotNull(groups = { EmailAuthenticationValidation.class })
	private Integer clientPlatformId;
	
	@NotNull(groups = { EmailAuthenticationValidation.class })
	private Integer externalNetworkId;

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public Integer getClientPlatformId() {
		return clientPlatformId;
	}

	public Integer getExternalNetworkId() {
		return externalNetworkId;
	}
}
