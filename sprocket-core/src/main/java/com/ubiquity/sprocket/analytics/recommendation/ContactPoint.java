package com.ubiquity.sprocket.analytics.recommendation;

import java.util.List;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;

import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Gender;

public class ContactPoint implements Function<Contact, Vector> {

	private static final long serialVersionUID = 1L;
	private List<Dimension> dimensions;
	
	protected ContactPoint(List<Dimension> dimensions) {
		this.dimensions = dimensions;
	}


	@Override
	public Vector call(Contact contact) {

		double[] point = computePoint(contact, dimensions);

		return Vectors.dense(point);
	}
	
	public static double[] computePoint(Contact contact, List<Dimension> dimensions) {
		double[] point = new double[1];

		// do gender
		Gender gender = contact.getGender();
		Dimension dimension = Dimension.findDimensionByAttribute("gender", dimensions);

		Double coordinates = Dimension.computeCoordinates(gender, dimension);
		point[0] = coordinates;
		
		return point;
	}
}
