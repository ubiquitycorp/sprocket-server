package com.ubiquity.sprocket.analytics.recommendation;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.Range;


public class Dimension implements Serializable {

	private static final long serialVersionUID = 1L;

	private Double weight = 1.0;

	private Range<Double> range;

	private String attribute;

	public Dimension(String attribute, Range<Double> range) {
		this.attribute = attribute;
		this.range = range;
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
	
	public static Dimension createFromEnum(String name, Class<? extends Enum<?>> enumClass) {
		Range<Double> range = Range.between(0.0, new Double(enumClass.getEnumConstants().length));
		return new Dimension(name, range);
	}
	
	public static Double computeCoordinates(Enum<?> fieldValue, Dimension dimension) {
		Range<Double> range = dimension.getRange();
		Double scaleRatio = 1 / (range.getMaximum() - range.getMinimum());
		return scaleRatio * (double)fieldValue.ordinal();
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

