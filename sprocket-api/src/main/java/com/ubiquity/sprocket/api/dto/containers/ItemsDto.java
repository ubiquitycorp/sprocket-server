package com.ubiquity.sprocket.api.dto.containers;

import java.util.LinkedList;
import java.util.List;

import com.ubiquity.sprocket.api.dto.model.ItemDto;

public class ItemsDto {

	private List<ItemDto> items = new LinkedList<ItemDto>();

	public List<ItemDto> getItems() {
		return items;
	}
}
