package com.ubiquity.sprocket.repository;

import java.util.List;

import com.niobium.repository.Repository;
import com.ubiquity.sprocket.domain.Location;


public interface LocationRepository extends Repository <Long, Location> {
	Location findByUserId(Long userId);
	
	List<Location> findAll();
}
