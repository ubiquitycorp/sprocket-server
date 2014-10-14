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
@Table(name="favorite_item")
public class FavoriteItem {
	@Id
	@GeneratedValue
	@Column(name = "favorite_item_id")
	private Long favoriteItemId;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	protected FavoriteItem() {}
	
	/**
	 * Parameterized constructor creates entity with required properties
	 * @param user
	 */
	protected FavoriteItem(User user) {
		this.user = user;
	}


	public Long getFavoriteItemId() {
		return favoriteItemId;
	}


	public User getUser() {
		return user;
	}
	
	

}
