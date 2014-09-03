package com.ubiquity.sprocket.analytics.recommendation;

import java.util.List;

import org.apache.commons.lang3.Range;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.social.domain.AgeRange;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Gender;

public class ContactFunction implements Function<Contact, Vector> {

	private static Logger log = LoggerFactory.getLogger(ContactFunction.class);

	private static final long serialVersionUID = 1L;
	private List<Dimension> dimensions;

	protected ContactFunction(List<Dimension> dimensions) {
		this.dimensions = dimensions;
	}

	@Override
	public Vector call(Contact contact) {
		// flatten contact into a feature vector
		double[] point = computePoint(contact, dimensions);
		return Vectors.dense(point);
	}

	/**
	 * Computes the N-dimensional position of this contact
	 * 
	 * @param contact
	 * @param dimensions
	 * 
	 * @return
	 */
	public static double[] computePoint(Contact contact, List<Dimension> dimensions) {
		double[] point = new double[2];

		// do gender
		Gender gender = contact.getGender();
		Dimension dimension = Dimension.findDimensionByAttribute("gender", dimensions);
		point[0] = (gender == null || dimension == null) ? 0.0 : Dimension.computeCoordinates(gender, dimension);
		log.debug("gender {} weight applied {}", point[0], dimension.getWeight());

		// age range
		AgeRange ageRange = contact.getAgeRange();
		dimension = Dimension.findDimensionByAttribute("ageRange", dimensions);
		point[1] = (ageRange == null || dimension == null) ? 0.0 : Dimension.computeCoordinates(fill(ageRange), dimension);
		log.debug("age range {} weight applied {}", point[1], dimension.getWeight());

		return point;
	}
	
	private static Range<Integer> fill(AgeRange ageRange) {
		Integer min = ageRange.getMin() == null ? 0 : ageRange.getMin();
		Integer max = ageRange.getMax() == null ? Integer.MAX_VALUE : ageRange.getMax();
		return Range.between(min, max);
	}
}
