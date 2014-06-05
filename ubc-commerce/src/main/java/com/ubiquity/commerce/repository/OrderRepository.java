package com.ubiquity.commerce.repository;

import java.util.List;

import com.niobium.repository.Repository;
import com.ubiquity.commerce.domain.Order;

/***
 * 
 * @author peter.tadros
 *
 */
public interface OrderRepository extends Repository <Long, Order>{
	List<Order> findSentOrdersByOwnerId(Long ownerId);
	List<Order> findReceivedOrdersByUserId(Long contactId);
}
