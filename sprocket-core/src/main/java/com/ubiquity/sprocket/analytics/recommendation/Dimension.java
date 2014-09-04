package com.ubiquity.sprocket.analytics.recommendation;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.Range;


public class Dimension implements Serializable {

	private static final long serialVersionUID = 1L;

	private Double weight = 1.0;

	private Range<Double> range;

	private String attribute;

	/**
	 * Creates a dimension for the specified attribute and range
	 * 
	 * @param attribute
	 * @param range
	 */
	public Dimension(String attribute, Range<Double> range) {
		this.attribute = attribute;
		this.range = range;
	}
	
	/***
	 * Creates a dimension for the specified attribute and range, and weight
	 * 
	 * @param attribute
	 * @param range
	 * @param weight
	 */
	public Dimension(String attribute, Range<Double> range, Double weight) {
		this.attribute = attribute;
		this.range = range;
		this.weight = weight;
	}
	
	public Double getWeight() {
		return weight;
	}

	public Range<Double> getRange() {
		return range;
	}

	public void setRange(Range<Double> range) {
		this.range = range;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attribute == null) ? 0 : attribute.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Dimension other = (Dimension) obj;
		if (attribute == null) {
			if (other.attribute != null)
				return false;
		} else if (!attribute.equals(other.attribute))
			return false;
		return true;
	}

	/**
	 * Creates a Dimension for the attribute name, computing the range from the length of the enumerated values
	 * 
	 * @param name
	 * @param enumClass
	 * @return
	 */
	public static Dimension createFromEnum(String name, Class<? extends Enum<?>> enumClass) {
		Range<Double> range = Range.between(0.0, new Double(enumClass.getEnumConstants().length));
		return new Dimension(name, range);
	}
	
	/***
	 * Creates a Dimension for the attribute name, computing the range from the length of the enumerated values
	 * and a weight value override
	 *
	 * @param name
	 * @param enumClass
	 * @param weight
	 * 
	 * @return
	 */
	public static Dimension createFromEnum(String name, Class<? extends Enum<?>> enumClass, Double weight) {
		Range<Double> range = Range.between(0.0, new Double(enumClass.getEnumConstants().length));
		return new Dimension(name, range, weight);
	}
	
	public static Double computeCoordinates(Enum<?> fieldValue, Dimension dimension) {
		Range<Double> range = dimension.getRange();
		Double scaleRatio = 1 / (range.getMaximum() - range.getMinimum());
		
		return (scaleRatio * (double)fieldValue.ordinal()) * dimension.getWeight();
	}
	
	public static Double computeCoordinates(Double fieldValue, Dimension dimension) {
		Range<Double> range = dimension.getRange();
		Double scaleRatio = 1 / (range.getMaximum() - range.getMinimum());
		
		return (scaleRatio * fieldValue) * dimension.getWeight();
	}
	
	

	/**
	 * Computes the coordinates by taking the min value and dividing by the max of the dimension
	 * 
	 * @param value
	 * @param dimension
	 * @return
	 */
	public static Double computeCoordinates(Range<? extends Number> value, Dimension dimension) {
		Range<Double> dimensionRange = dimension.getRange(); // declared boundaries for range
		
		// min value of of the actual range
		Double mininum = value.getMinimum().doubleValue();
		
		// compute scale ratio
		Double scaleRatio = 1 / (dimensionRange.getMaximum() - dimensionRange.getMinimum());
	
		return (scaleRatio * mininum) * dimension.getWeight();
	}
	
	public static Dimension findDimensionByAttribute(String attribute, List<Dimension> dimensions) {
		for(Dimension dimention : dimensions) {
			if(dimention.getAttribute().equalsIgnoreCase(attribute)) {
				return dimention;
			}	
		}
		return null;
	}


}

