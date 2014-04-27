package com.ubiquity.commerce.domain;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.Table;

@Entity
@Inheritance
@DiscriminatorColumn(name="item_option_value_type")
@Table(name="item_option")
public abstract class ItemOption {
	
	@Id
	@GeneratedValue
	@Column(name = "item_option_id")
	private Long itemId;
	
}
