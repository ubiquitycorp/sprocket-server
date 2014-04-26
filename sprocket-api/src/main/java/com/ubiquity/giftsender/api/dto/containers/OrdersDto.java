package com.ubiquity.giftsender.api.dto.containers;

import java.util.LinkedList;
import java.util.List;

import com.ubiquity.giftsender.api.dto.model.OrderDto;

/***
 * Result payload returns a list of orders
 * 
 * @author chris
 *
 */
public class OrdersDto {
	
	private List<OrderDto> orders = new LinkedList<OrderDto>();

	public List<OrderDto> getOrders() {
		return orders;
	}

}
