package com.ubiquity.giftsender.api.dto.containers;

import java.util.LinkedList;
import java.util.List;

import com.ubiquity.giftsender.api.dto.model.ItemDto;

public class ItemsDto {

	private List<ItemDto> items = new LinkedList<ItemDto>();

	public List<ItemDto> getItems() {
		return items;
	}
}
