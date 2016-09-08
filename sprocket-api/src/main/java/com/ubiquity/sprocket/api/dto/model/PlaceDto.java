package com.ubiquity.sprocket.api.dto.model;

import java.util.HashSet;
import java.util.Set;

import com.ubiquity.sprocket.api.dto.model.media.ImageDto;
import com.ubiquity.sprocket.api.dto.model.social.RatingDto;


public class PlaceDto {

	private Long placeId;
	private String name;
	private String description;
	private String region;
	private GeoboxDto boundingBox;
	private Set<InterestDto> interests = new HashSet<InterestDto>();
	private Long lastUpdated;
	private PlaceDto parent;
	private String externalIdentitifer;
	private Integer externalNetworkId;
	private AddressDto addressdto;
	private RatingDto ratingDto;
	private ImageDto thumb;
	private String locator;
	
	
	public Long getPlaceId() {
		return placeId;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getRegion() {
		return region;
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

	public Integer getExternalNetworkId() {
		return externalNetworkId;
	}

	public AddressDto getAddressdto() {
		return addressdto;
	}

	public RatingDto getRatingDto() {
		return ratingDto;
	}
	
	public ImageDto getThumb() {
		return thumb;
	}
	
	public String getLocator() {
		return locator;
	}
	
	public static class Builder {
		private Long placeId;
		private String name;
		private String description;
		private String region;
		private GeoboxDto boundingBox;
		private String externalIdentitifer;
		private Integer externalNetworkId;
		private AddressDto addressdto;
		private Long lastUpdated;
		private PlaceDto parent;
		private RatingDto ratingDto;
		private ImageDto thumb;
		private String locator;
		
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

		public Builder region(String region) {
			this.region = region;
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

		public Builder network(Integer externalNetworkId) {
			this.externalNetworkId = externalNetworkId;
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
		
		public Builder ratingDto(RatingDto ratingDto) {
			this.ratingDto = ratingDto;
			return this;
		}
		
		public Builder parent(PlaceDto parent){
			this.parent = parent;
			return this;
		}
		public Builder thumb(ImageDto thumb){
			this.thumb= thumb;
			return this;
		}
		public Builder locator(String locator){
			this.locator= locator;
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
		this.region = builder.region;
		this.boundingBox = builder.boundingBox;
		this.externalIdentitifer = builder.externalIdentitifer;
		this.externalNetworkId = builder.externalNetworkId;
		this.addressdto = builder.addressdto;
		this.lastUpdated = builder.lastUpdated;
		this.parent = builder.parent;
		this.ratingDto = builder.ratingDto;
		this.thumb = builder.thumb;
		this.locator = builder.locator;
	}
}
