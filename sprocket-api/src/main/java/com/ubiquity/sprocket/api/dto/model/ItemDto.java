package com.ubiquity.sprocket.api.dto.model;

import java.util.List;
import java.util.Map;

public class ItemDto {

	private Long itemId;
	private String itemName;
	private String description;
	private Double unitPrice;
	private String imageUrl;
	private Map<String, List<Object>> options;
	private String etag;

	/***
	 * Default constructor required by JPA
	 */
	public ItemDto() {
	}

	public String getEtag() {
		return etag;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setOptions(Map<String, List<Object>> options) {
		this.options = options;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getImage() {
		return imageUrl;
	}

	public void setImage(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public Map<String, List<Object>> getOptions() {
		return options;
	}

	public static class Builder {
		private Long itemId;
		private String itemName;
		private String description;
		private Double unitPrice;
		private String imageUrl;
		private Map<String, List<Object>> options;
		private String etag;

		public Builder itemId(Long itemId) {
			this.itemId = itemId;
			return this;
		}

		public Builder itemName(String itemName) {
			this.itemName = itemName;
			return this;
		}

		public Builder description(String description) {
			this.description = description;
			return this;
		}

		public Builder unitPrice(Double unitPrice) {
			this.unitPrice = unitPrice;
			return this;
		}

		public Builder imageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
			return this;
		}

		public Builder options(Map<String, List<Object>> options) {
			this.options = options;
			return this;
		}

		public Builder etag(String etag) {
			this.etag = etag;
			return this;
		}

		public ItemDto build() {
			return new ItemDto(this);
		}
	}

	private ItemDto(Builder builder) {
		this.itemId = builder.itemId;
		this.itemName = builder.itemName;
		this.description = builder.description;
		this.unitPrice = builder.unitPrice;
		this.imageUrl = builder.imageUrl;
		this.options = builder.options;
		this.etag = builder.etag;
	}
}
