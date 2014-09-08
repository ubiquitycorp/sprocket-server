package com.ubiquity.sprocket.domain;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ubiquity.identity.domain.User;

@Entity
@Inheritance
@DiscriminatorColumn(name="item_type")
@Table(name="engaged_item")
public class EngagedItem {
	
	@Id
	@GeneratedValue
	@Column(name = "engaged_item_id")
	private Long engagedItemId;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	protected EngagedItem() {}
	
	/**
	 * Parameterized constructor creates entity with required properties
	 * @param user
	 */
	protected EngagedItem(User user) {
		this.user = user;
	}


	public Long getEngagedItemId() {
		return engagedItemId;
	}


	public User getUser() {
		return user;
	}
	
	
}
