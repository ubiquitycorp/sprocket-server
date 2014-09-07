package com.ubiquity.sprocket.repository;

import java.util.Locale;

import com.niobium.repository.Repository;
import com.ubiquity.sprocket.domain.Location;
import com.ubiquity.sprocket.domain.Place;


public interface PlaceRepository extends Repository <Long, Place> {
	/**
	 * Finds a place by its name
	 * 
	 * @param name
	 * @param locale
	 * 
	 * @return a place or null if it doesn't exist
	 */
	Place findByName(String name, Locale locale);
}
