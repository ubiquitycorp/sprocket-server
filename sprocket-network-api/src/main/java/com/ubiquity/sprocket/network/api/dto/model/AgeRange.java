package com.ubiquity.sprocket.network.api.dto.model;

import java.io.Serializable;

public class AgeRange implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Integer min;
	
	private Integer max;
	
	protected AgeRange() {}
	
	/***
	 * Constructs age range min and max
	 * 
	 * @param min
	 * @param max
	 */
	public AgeRange(Integer min, Integer max) {
		this.min = min;
		this.max = max;
	}
	
	public Integer getMin() {
		return min;
	}
	public Integer getMax() {
		return max;
	}
	
	

}
