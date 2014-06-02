package com.ubiquity.commerce.domain;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.ubiquity.media.domain.Image;

@Entity
@Table(name = "item")
public class Item {

	@Id
	@GeneratedValue
	@Column(name = "item_id")
	private Long itemId;

	@Column(name = "item_name", nullable = false)
	private String itemName;

	@Column(name = "description", length = 500, nullable = true)
	private String description;

	@Column(name = "unit_price", nullable = false)
	private double unitPrice;

	@OneToMany(mappedBy="item", fetch = FetchType.EAGER, cascade = { CascadeType.ALL }, orphanRemoval = true)
	@MapKey(name="type")
	private Map<String, ItemOptionType> options = new HashMap<String, ItemOptionType>();
	
	@Embedded
	private Image image;

	@Column(name = "rate")
	private Integer rate;

	@ManyToOne
	@JoinColumn(name = "store_id", nullable = true)
	private Store store;

	/***
	 * Default constructor required by JPA
	 */
	public Item() {
	}

	
	public Map<String, ItemOptionType> getOptions() {
		return options;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Integer getRate() {
		return rate;
	}

	public void setRate(Integer rate) {
		this.rate = rate;
	}

	public Store getStore() {
		return store;
	}


	public void setStore(Store store) {
		this.store = store;
	}



	public static class Builder {
		private Long itemId;
		private String itemName;
		private String description;
		private double unitPrice;
		private Image image;
		private Integer rate;
		private Store store;
		
		public Builder itemId(Long itemId) {
			this.itemId = itemId;
			return this;
		}

		public Builder itemName(String itemName) {
			this.itemName = itemName;
			return this;
		}

		public Builder description(String description) {
			this.description = description;
			return this;
		}

		public Builder unitPrice(double unitPrice) {
			this.unitPrice = unitPrice;
			return this;
		}

		public Builder image(Image image) {
			this.image = image;
			return this;
		}

		public Builder rate(Integer rate) {
			this.rate = rate;
			return this;
		}

		public Builder store(Store store) {
			this.store = store;
			return this;
		}
		
		public Item build() {
			return new Item(this);
		}
	}

	private Item(Builder builder) {
		this.itemId = builder.itemId;
		this.itemName = builder.itemName;
		this.description = builder.description;
		this.unitPrice = builder.unitPrice;
		this.image = builder.image;
		this.rate = builder.rate;
		this.store = builder.store;
	}
}
