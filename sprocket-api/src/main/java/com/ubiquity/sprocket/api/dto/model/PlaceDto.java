package com.ubiquity.sprocket.api.dto.model;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.location.domain.Geobox;


public class PlaceDto {

	private Long placeId;
	private String name;
	private String description;
	private Locale locale;
	private GeoboxDto boundingBox;
	private Set<InterestDto> interests = new HashSet<InterestDto>();
	private Long lastUpdated;
	private PlaceDto parent;
	private String externalIdentitifer;
	private ExternalNetwork network;
	private AddressDto addressdto;
	
	
	
	public Long getPlaceId() {
		return placeId;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Locale getLocale() {
		return locale;
	}

	public GeoboxDto getBoundingBox() {
		return boundingBox;
	}

	public Set<InterestDto> getInterests() {
		return interests;
	}

	public Long getLastUpdated() {
		return lastUpdated;
	}

	public PlaceDto getParent() {
		return parent;
	}

	public String getExternalIdentitifer() {
		return externalIdentitifer;
	}

	public ExternalNetwork getNetwork() {
		return network;
	}

	public AddressDto getAddressdto() {
		return addressdto;
	}

	public static class Builder {
		private Long placeId;
		private String name;
		private String description;
		private Locale locale;
		private GeoboxDto boundingBox;
		private String externalIdentitifer;
		private ExternalNetwork network;
		private AddressDto addressdto;
		private Long lastUpdated;
		private PlaceDto parent;
		
		public Builder placeId(Long placeId) {
			this.placeId = placeId;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder description(String description) {
			this.description = description;
			return this;
		}

		public Builder locale(Locale locale) {
			this.locale = locale;
			return this;
		}

		public Builder boundingBox(GeoboxDto boundingBox) {
			this.boundingBox = boundingBox;
			return this;
		}

		public Builder externalIdentitifer(String externalIdentitifer) {
			this.externalIdentitifer = externalIdentitifer;
			return this;
		}

		public Builder network(ExternalNetwork network) {
			this.network = network;
			return this;
		}

		public Builder addressdto(AddressDto addressdto) {
			this.addressdto = addressdto;
			return this;
		}
		
		public Builder lastUpdated(Long lastUpdated) {
			this.lastUpdated = lastUpdated;
			return this;
		}
		
		public Builder parent(PlaceDto parent){
			this.parent = parent;
			return this;
		}
		public PlaceDto build() {
			return new PlaceDto(this);
		}
	}

	private PlaceDto(Builder builder) {
		this.placeId = builder.placeId;
		this.name = builder.name;
		this.description = builder.description;
		this.locale = builder.locale;
		this.boundingBox = builder.boundingBox;
		this.externalIdentitifer = builder.externalIdentitifer;
		this.network = builder.network;
		this.addressdto = builder.addressdto;
		this.lastUpdated = builder.lastUpdated;
		this.parent = builder.parent;
	}
}
