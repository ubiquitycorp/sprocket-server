package com.ubiquity.social.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ubiquity.identity.domain.User;
import com.ubiquity.media.domain.Image;

@Entity
@Table(name = "activity")
public class Activity {

	@Id
	@GeneratedValue
	@Column(name = "activity_id")
	private Long activityId;

	@Column(name = "title", length = 150, nullable = true)
	private String title;

	@Lob
	@Column(name = "body", nullable = true)
	private String body;

	@Column(name = "type", length = 150, nullable = true)
	private String type;

	@Column(name = "creation_date", nullable = true)
	private Long creationDate;

	@Column(name = "last_updated", nullable = false)
	private Long lastUpdated;

	@ManyToOne
	@JoinColumn(name = "owner_id", nullable = false)
	private User owner;

	@ManyToOne
	@JoinColumn(name = "posted_by_contact_id", nullable = true)
	private Contact postedBy;

	@Embedded
	private Image image;

	/**
	 * Default constructor required by JPA
	 */
	protected Activity() {
	}

	public Long getActivityId() {
		return activityId;
	}

	public String getTitle() {
		return title;
	}

	public String getBody() {
		return body;
	}

	public User getOwner() {
		return owner;
	}

	public String getType() {
		return type;
	}

	public Long getCreationDate() {
		return creationDate;
	}

	public Long getLastUpdated() {
		return lastUpdated;
	}

	public Image getImage() {
		return image;
	}

	public Contact getPostedBy() {
		return postedBy;
	}

	public static class Builder {
		private Long activityId;
		private String title;
		private String body;
		private String type;
		private Long creationDate;
		private Long lastUpdated;
		private User owner;
		private Contact postedBy;
		private Image image;

		public Builder activityId(Long activityId) {
			this.activityId = activityId;
			return this;
		}

		public Builder title(String title) {
			this.title = title;
			return this;
		}

		public Builder body(String body) {
			this.body = body;
			return this;
		}

		public Builder type(String type) {
			this.type = type;
			return this;
		}

		public Builder creationDate(Long creationDate) {
			this.creationDate = creationDate;
			return this;
		}

		public Builder lastUpdated(Long lastUpdated) {
			this.lastUpdated = lastUpdated;
			return this;
		}

		public Builder owner(User owner) {
			this.owner = owner;
			return this;
		}

		public Builder postedBy(Contact postedBy) {
			this.postedBy = postedBy;
			return this;
		}

		public Builder image(Image image) {
			this.image = image;
			return this;
		}

		public Activity build() {
			return new Activity(this);
		}
	}

	private Activity(Builder builder) {
		this.activityId = builder.activityId;
		this.title = builder.title;
		this.body = builder.body;
		this.type = builder.type;
		this.creationDate = builder.creationDate;
		this.lastUpdated = builder.lastUpdated;
		this.owner = builder.owner;
		this.postedBy = builder.postedBy;
		this.image = builder.image;
	}
}