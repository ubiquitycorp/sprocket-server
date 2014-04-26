package com.ubiquity.giftsender.api.dto.model;

/***
 * 
 * @author peter.tadros
 *
 */
public class AccountDto {

	private Long userId;
	
	private String accessToken;
		
	private Integer providerId;
	
	private String secretToken;
	
	private String apiKey;
	
	private Integer clientPlatformId;
	
	public String getAccessToken() {
		return accessToken;
	}
	public String getSecretToken() {
		return secretToken;
	}
	public Integer getProviderId() {
		return providerId;
	}
	public Long getUserId() {
		return userId;
	}
	public String getApiKey() {
		return apiKey;
	}
	public Integer getClientPlatformId() {
		return clientPlatformId;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
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
	
	public void setClientPlatformId(Integer clientPlatform) {
		this.clientPlatformId = clientPlatform;
	}
}
