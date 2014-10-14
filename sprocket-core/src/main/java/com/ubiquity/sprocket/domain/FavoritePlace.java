package com.ubiquity.sprocket.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.ubiquity.identity.domain.User;
import com.ubiquity.location.domain.Place;

@Entity
@DiscriminatorValue("place")
public class FavoritePlace extends FavoriteItem {
	@ManyToOne
	@JoinColumn(name = "place_id")	
	private Place place;
	
	protected FavoritePlace() {}
	
	public FavoritePlace(User user, Place place) {
		super(user);
		this.place = place;
	}

	public Place getPlace() {
		return place;
	}
	
}
