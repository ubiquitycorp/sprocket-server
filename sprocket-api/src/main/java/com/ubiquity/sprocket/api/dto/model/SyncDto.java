package com.ubiquity.sprocket.api.dto.model;

import com.esotericsoftware.kryo.NotNull;

public class SyncDto {
	@NotNull
	private Integer externalNetworkId;
	@NotNull
	private Integer clientPlatformId;
	
	public Integer getExternalNetworkId() {
		return externalNetworkId;
	}
	public Integer getClientPlatformId() {
		return clientPlatformId;
	}
}
