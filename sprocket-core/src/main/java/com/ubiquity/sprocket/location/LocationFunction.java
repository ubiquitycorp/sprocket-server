package com.ubiquity.sprocket.location;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.sprocket.domain.Location;

public class LocationFunction implements Function<Location, Vector> {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(LocationFunction.class);

	private static final long serialVersionUID = 1L;

	@Override
	public Vector call(Location location) {
		// flatten contact into a feature vector
		double[] point = computePoint(location);
		return Vectors.dense(point);
	}

	/**
	 * Computes the N-dimensional position of this contact
	 * 
	 * @param location
s	 * 
	 * @return
	 */
	public static double[] computePoint(Location location) {
		return new double[] {
			location.getLatitude(), location.getLongitude()
		};
	}
}
