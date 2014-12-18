package com.ubiquity.sprocket.api.dto.model;

import com.ubiquity.integration.domain.ExternalNetwork;

public class ExternalInterestDto {

	private Long externalInterestId;

	private String name;

	private InterestDto interestDto;

	private Integer externalNetworkId;


	/***
	 * Creates an interest mapping with required properties
	 * 
	 * @param name
	 * @param interest
	 * @param externalNetwork
	 */
	public ExternalInterestDto(String name, InterestDto interestDto,
			ExternalNetwork externalNetwork) {
		super();
		this.name = name;
		this.interestDto = interestDto;
		this.externalNetworkId = ExternalNetwork.ordinalOrDefault(externalNetwork);
	}

	public String getName() {
		return name;
	}

	public InterestDto getInterestDto() {
		return interestDto;
	}

	public ExternalNetwork getExternalNetwork() {
		return ExternalNetwork.getNetworkById(externalNetworkId);
	}

	public Long getExternalInterestId() {
		return externalInterestId;
	}



}
