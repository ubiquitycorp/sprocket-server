package com.ubiquity.social.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ubiquity.identity.domain.User;

@Entity
@Table(name = "event")
public class Event {

	@Id
	@GeneratedValue
	@Column(name = "event_id")
	private Long eventId;

	@Column(name = "name", length = 150, nullable = false)
	private String name;

	@Column(name = "description", length = 255, nullable = true)
	private String description;

	@Column(name = "start_date", nullable = false)
	private Long startDate;

	@Column(name = "end_date", nullable = true)
	private Long endDate;

	@ManyToOne
	@JoinColumn(name = "contact_id", nullable = true)
	private Contact contact;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "social_provider_identifier", nullable = true)
	private String socialProviderIdentifier;

	@Column(name = "last_updated", nullable = false)
	private Long lastUpdated;

	protected Event() {
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public User getUser() {
		return user;
	}

	public Long getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Long lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Long getEventId() {
		return eventId;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Contact getContact() {
		return contact;
	}

	public Long getStartDate() {
		return startDate;
	}

	public Long getEndDate() {
		return endDate;
	}

	public String getSocialProviderIdentifier() {
		return socialProviderIdentifier;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((socialProviderIdentifier == null) ? 0
						: socialProviderIdentifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Event other = (Event) obj;
		if (socialProviderIdentifier == null) {
			if (other.socialProviderIdentifier != null)
				return false;
		} else if (!socialProviderIdentifier
				.equals(other.socialProviderIdentifier))
			return false;
		return true;
	}

	public static class Builder {
		private Long eventId;
		private String name;
		private String description;
		private Long startDate;
		private Long endDate;
		private Contact contact;
		private User user;
		private String socialProviderIdentifier;
		private Long lastUpdated;

		public Builder eventId(Long eventId) {
			this.eventId = eventId;
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

		public Builder startDate(Long startDate) {
			this.startDate = startDate;
			return this;
		}

		public Builder endDate(Long endDate) {
			this.endDate = endDate;
			return this;
		}

		public Builder contact(Contact contact) {
			this.contact = contact;
			return this;
		}

		public Builder user(User user) {
			this.user = user;
			return this;
		}

		public Builder socialProviderIdentifier(String socialProviderIdentifier) {
			this.socialProviderIdentifier = socialProviderIdentifier;
			return this;
		}

		public Builder lastUpdated(Long lastUpdated) {
			this.lastUpdated = lastUpdated;
			return this;
		}

		public Event build() {
			return new Event(this);
		}
	}

	private Event(Builder builder) {
		this.eventId = builder.eventId;
		this.name = builder.name;
		this.description = builder.description;
		this.startDate = builder.startDate;
		this.endDate = builder.endDate;
		this.contact = builder.contact;
		this.user = builder.user;
		this.socialProviderIdentifier = builder.socialProviderIdentifier;
		this.lastUpdated = builder.lastUpdated;
	}
}
