package com.ubiquity.sprocket.api.dto.model;

import com.esotericsoftware.kryo.NotNull;
import com.ubiquity.integration.domain.AdminInterestType;
import com.ubiquity.integration.domain.ExternalNetwork;

public class AdminInterestDto {

	private Long id;
	
	@NotNull
	private String name;

	private Long interestId;
	
	private Long parentInterestId;
	
	@NotNull
	private Integer externalNetworkId;
	
	@NotNull
	private Integer interestTypeId;

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Long getInterestId() {
		return interestId;
	}

	public Long getParentInterestId() {
		return parentInterestId;
	}

	public ExternalNetwork getExternalNetwork() {
		return ExternalNetwork.getNetworkById(externalNetworkId);
	}
	
	public Integer getInterestType() {
		return interestTypeId;
	}
	public static class Builder{
		private Long id;

		private String name;

		private Long interestId;
		
		private Long parentInterestId;

		private Integer externalNetworkId;
		
		private Integer interestTypeId;
		
		public Builder id(Long id){
			this.id =id;
			return this;
		}
		
		public Builder name(String name){
			this.name =name;
			return this;
		}
		
		public Builder interestId(Long interestId){
			this.interestId =interestId;
			return this;
		}
		
		public Builder parentInterestId(Long parentInterestId){
			this.parentInterestId =parentInterestId;
			return this;
		}
		
		public Builder externalNetworkId(ExternalNetwork externaNetwork){
			this.externalNetworkId =ExternalNetwork.ordinalOrDefault(externaNetwork);
			return this;
		}
		
		public Builder interestType(AdminInterestType interestType){
			this.interestTypeId= interestType.ordinal();
			return this;
		}
		
		public AdminInterestDto build(){
			return new AdminInterestDto(this);
		}
	}
	public AdminInterestDto(Builder builder){
		this.id= builder.id;
		this.name = builder.name;
		this.interestId = builder.interestId;
		this.parentInterestId = builder.parentInterestId;
		this.externalNetworkId = builder.externalNetworkId;
		this.interestTypeId = builder.interestTypeId;
	}

}
