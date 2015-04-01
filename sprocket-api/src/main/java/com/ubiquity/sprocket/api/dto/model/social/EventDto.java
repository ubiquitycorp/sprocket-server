package com.ubiquity.sprocket.api.dto.model.social;


public class EventDto {

	private Long eventId;
	private String name;
	private Long startDate;
	private Long endDate;
	private String imageUrl;
	private ContactDto owner;

	public EventDto(Long eventId, String name, Long startDate,
			Long endDate, ContactDto owner, String image) {
		this.eventId = eventId;
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
		this.imageUrl = image;
		this.owner = owner;
	}

	public ContactDto getOwner() {
		return owner;
	}

	public Long getEventId() {
		return eventId;
	}

	public String getName() {
		return name;
	}

	public Long getStartDate() {
		return startDate;
	}

	public Long getEndDate() {
		return endDate;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public static class Builder {
		private Long eventId;
		private String name;
		private Long startDate;
		private Long endDate;
		private String imageUrl;
		private ContactDto owner;

		public Builder eventId(Long eventId) {
			this.eventId = eventId;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder startDate(Long startDate) {
			this.startDate = startDate;
			return this;
		}

		public Builder endDate(Long endDate) {
			this.endDate = endDate;
			return this;
		}

		public Builder imageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
			return this;
		}

		public Builder owner(ContactDto owner) {
			this.owner = owner;
			return this;
		}

		public EventDto build() {
			return new EventDto(this);
		}
	}

	private EventDto(Builder builder) {
		this.eventId = builder.eventId;
		this.name = builder.name;
		this.startDate = builder.startDate;
		this.endDate = builder.endDate;
		this.imageUrl = builder.imageUrl;
		this.owner = builder.owner;
	}
}
