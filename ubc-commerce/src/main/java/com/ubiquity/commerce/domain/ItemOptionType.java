package com.ubiquity.commerce.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "item_option_type")
public class ItemOptionType {
	
	@Id
	@GeneratedValue
	private Long id;
	 
	@ManyToOne
	@JoinColumn(name = "item_id")
	private Item item;
	 
	@Column(name="option_type")
	private String type;
	
	/***
	 * No-arg constructor required by JPA
	 */
	protected ItemOptionType() {}
	
	/***
	 * Parameterized constructor creates entity with required value
	 * 
	 * @param item
	 * @param type
	 */
	public ItemOptionType(Item item, String type) {
		this.item = item;
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public Item getItem() {
		return item;
	}

	public String getType() {
		return type;
	}

	public Set<ItemOption> getOptions() {
		return options;
	}



	@ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
	private Set<ItemOption> options = new HashSet<ItemOption>();
	
	

}
