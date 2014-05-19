package com.ubiquity.social.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ubiquity.identity.domain.User;

@Entity
@Table(name = "message")
public class Message {

	@Id
	@GeneratedValue
	@Column(name = "message_id")
	private Long messageId;

	@Column(name = "title", length = 150, nullable = true)
	private String title;

	@Lob
	@Column(name = "body", nullable = true)
	private String body;

	@Column(name = "send_date", nullable = false)
	private Long sentDate;

	@ManyToOne
	@JoinColumn(name = "sender_contact_id", nullable = true)
	private Contact sender;

	@ManyToOne
	@JoinColumn(name = "owner_id", nullable = false)
	private User owner;

	/**
	 * Default constructor required by JPA
	 */
	protected Message() {}
	
	public Long getMessageId() {
		return messageId;
	}

	public String getTitle() {
		return title;
	}

	public String getBody() {
		return body;
	}

	public Long getSentDate() {
		return sentDate;
	}

	public Contact getSender() {
		return sender;
	}

	public User getOwner() {
		return owner;
	}

	public static class Builder {
		private Long messageId;
		private String title;
		private String body;
		private Long sentDate;
		private Contact sender;
		private User owner;

		public Builder messageId(Long messageId) {
			this.messageId = messageId;
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

		public Builder sentDate(Long sentDate) {
			this.sentDate = sentDate;
			return this;
		}

		public Builder sender(Contact sender) {
			this.sender = sender;
			return this;
		}

		public Builder owner(User owner) {
			this.owner = owner;
			return this;
		}

		public Message build() {
			return new Message(this);
		}
	}

	private Message(Builder builder) {
		this.messageId = builder.messageId;
		this.title = builder.title;
		this.body = builder.body;
		this.sentDate = builder.sentDate;
		this.sender = builder.sender;
		this.owner = builder.owner;
	}
}