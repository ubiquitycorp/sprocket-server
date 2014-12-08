package com.ubiquity.sprocket.api.dto.model;

import javax.validation.constraints.NotNull;
/***
 * 
 * @author peter.tadros
 *
 */
public class AudioDto {

	private String url;
	private String itemKey;
	private String embedCode;
	
	/***
	 * Constraints applied when this DTO is used to carry an input payload; these 3 properties are needed to determine
	 * if a record has already been persisted
	 */
	@NotNull
	private Integer externalNetworkId;
	
	@NotNull
	private String title;
	
	@NotNull
	private String description;
	
	private String category;
	
	
	private Long lastUpdated;
	private ImageDto thumb;
	
	private Long ownerId;

	public ImageDto getThumb() {
		return thumb;
	}

	public String getUrl() {
		return url;
	}

	public String getItemKey() {
		return itemKey;
	}

	public Integer getExternalNetworkId() {
		return externalNetworkId;
	}
	
	public Long getLastUpdated() {
		return lastUpdated;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getCategory() {
		return category;
	}
	
	public Long getOwnerId(){
		return ownerId;
	}

	public String getEmbedCode() {
		return embedCode;
	}

	public static class Builder {
		private String url;
		private String itemKey;
		private Integer externalNetworkId;
		private Long lastUpdated;
		private String title;
		private String description;
		private String category;
		private ImageDto thumb;
		private Long ownerId;
		private String embedCode;
		
		public Builder url(String url) {
			this.url = url;
			return this;
		}

		public Builder itemKey(String itemKey) {
			this.itemKey = itemKey;
			return this;
		}

		public Builder externalNetworkId(Integer externalNetworkId) {
			this.externalNetworkId = externalNetworkId;
			return this;
		}
		
		public Builder lastUpdated(Long lastUpdated) {
			this.lastUpdated = lastUpdated;
			return this;
		}

		public Builder title(String title) {
			this.title = title;
			return this;
		}
		
		public Builder embedCode(String embedCode){
			this.embedCode = embedCode;
			return this;
		}
		public Builder description(String description) {
			this.description = description;
			return this;
		}

		public Builder category(String category) {
			this.category = category;
			return this;
		}

		public Builder thumb(ImageDto thumb) {
			this.thumb = thumb;
			return this;
		}
		
		public Builder ownerId(Long ownerId){
			this.ownerId = ownerId;
			return this;
		}

		public AudioDto build() {
			return new AudioDto(this);
		}
	}

	private AudioDto(Builder builder) {
		this.url = builder.url;
		this.itemKey = builder.itemKey;
		this.externalNetworkId = builder.externalNetworkId;
		this.lastUpdated = builder.lastUpdated;
		this.title = builder.title;
		this.description = builder.description;
		this.category = builder.category;
		this.thumb = builder.thumb;
		this.ownerId = builder.ownerId;
		this.embedCode = builder.embedCode;
	}
}
