package com.ubiquity.commerce.domain;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("S")
public class ItemStringOption extends ItemOption {

	@Column(name="string_value", unique = true)
	private String value;

	public String getValue() {
		return value;
	}
	
	protected ItemStringOption() {
		super();
	}
	
	public ItemStringOption(String value) {
		this.value = value;
	}
	
	
}
