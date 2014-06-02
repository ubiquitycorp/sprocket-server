package com.ubiquity.commerce.domain;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("D")
public class ItemDoubleOption extends ItemOption {

	@Column(name="double_value", unique = true)
	private Double value;

	public Double getValue() {
		return value;
	}

	protected ItemDoubleOption() {
		super();
	}
	
	public ItemDoubleOption(Double value) {
		this.value = value;
	}
	
	
	
}
