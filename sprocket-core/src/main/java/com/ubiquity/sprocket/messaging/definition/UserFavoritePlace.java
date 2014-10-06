package com.ubiquity.sprocket.messaging.definition;

import com.ubiquity.location.domain.Place;

public class UserFavoritePlace {
	private Long userId;
	private Place place;

	public UserFavoritePlace(Long userId, Place place) {
		super();
		this.userId = userId;
		this.place = place;
	}

	public Long getUserId() {
		return userId;
	}

	public Place getPlace() {
		return place;
	}

	@Override
	public String toString() {
		return "UserFavoritePlace [userId=" + userId + ", place="
				+ place + "]";
	}


}
