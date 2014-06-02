package com.ubiquity.commerce.repository;

import java.util.List;

import com.niobium.repository.Repository;
import com.ubiquity.commerce.domain.Store;

/***
 * 
 * Interface exposing CRUD methods for the Store entity
 * 
 * @author peter.tadros
 *
 */
public interface StoreRepository extends Repository <Long, Store> {
	
	public List<Store> findAll();
}

