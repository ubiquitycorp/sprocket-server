package com.ubiquity.sprocket.api.dto.containers;

import java.util.LinkedList;
import java.util.List;

import com.ubiquity.sprocket.api.dto.model.CategoryDto;


public class CategoriesDto {
	
	private List<CategoryDto> categories = new LinkedList<CategoryDto>();

	public List<CategoryDto> getCategories() {
		return categories;
	}
}
