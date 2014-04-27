package com.ubiquity.commerce.repository;

import java.util.List;

import com.niobium.repository.Repository;
import com.ubiquity.commerce.domain.Item;

/***
 * 
 * @author peter.tadros
 *
 */
public interface ItemRepository extends Repository <Long, Item>{
	List<Item> findAll();
}
