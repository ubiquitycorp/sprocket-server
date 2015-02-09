package com.ubiquity.sprocket.domain;

import java.io.Serializable;

import com.ubiquity.integration.domain.ExternalNetwork;

public class ProfilePK implements Serializable {
	
	private static final long serialVersionUID = -9137079810637975578L;

	private static final String COMPOSITE_KEY_PREFIX_INTERNAL_NETWORK = "sprocket";
	
	private ExternalNetwork externalNetwork;
	private Long userId;
	
	/***
	 * Initializes a PK for an identity with an external network
	 * @param externalNetwork
	 * @param userId
	 */
	public ProfilePK(ExternalNetwork externalNetwork, Long userId) {
		this.externalNetwork = externalNetwork;
		this.userId = userId;
	}


	/***
	 * Initializes a PK for the central profile
	 * 
	 * @param userId
	 */
	public ProfilePK(Long userId) {
		this.userId = userId;
	}
	
	public ExternalNetwork getExternalNetwork() {
		return externalNetwork;
	}
	
	public Long getUserId() {
		return userId;
	}
	
	@Override
	public String toString() {
		return externalNetwork == null ? COMPOSITE_KEY_PREFIX_INTERNAL_NETWORK + "-" + userId : externalNetwork.name() + "-" + userId;
	}
	
	
	
}