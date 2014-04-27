package com.ubiquity.sprocket.api.dto.model;

/***
 * 
 * @author peter.tadros
 *
 */
public class AccountDto {

	private Long userId;
	
	private String username;
	
	private String password;
	
	private String accessToken;
		
	private Integer providerId;
	
	private String secretToken;
	
	private String apiKey;
	
	private String displayName;
	
	private Integer clientPlatformId;

	public Long getUserId() {
		return userId;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public Integer getProviderId() {
		return providerId;
	}

	public String getSecretToken() {
		return secretToken;
	}

	public String getApiKey() {
		return apiKey;
	}

	public Integer getClientPlatformId() {
		return clientPlatformId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public void setProviderId(Integer providerId) {
		this.providerId = providerId;
	}

	public void setSecretToken(String secretToken) {
		this.secretToken = secretToken;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setClientPlatformId(Integer clientPlatformId) {
		this.clientPlatformId = clientPlatformId;
	}
	
	
	
}
