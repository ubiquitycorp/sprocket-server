package com.ubiquity.sprocket.repository.hbase;

import java.io.Serializable;

import com.ubiquity.integration.domain.ExternalNetwork;

public class ProfilePK implements Serializable {
	
	private static final long serialVersionUID = -9137079810637975578L;

	private static final String INTERNAL_NETWORK_NAME = "sprocket";
	
	private ExternalNetwork externalNetwork;
	private Long userId;
	
	public ProfilePK(ExternalNetwork externalNetwork, Long userId) {
		this.externalNetwork = externalNetwork;
		this.userId = userId;
	}
	
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
		return externalNetwork == null ? INTERNAL_NETWORK_NAME + "-" + userId : externalNetwork.name() + "-" + userId;
	}
	
	
	
}