package com.ubiquity.sprocket.analytics.recommendation;

import java.util.List;

import org.apache.commons.lang3.Range;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.social.domain.AgeRange;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Gender;
import com.ubiquity.sprocket.domain.UserLocation;

public class ContactFunction implements Function<Profile, Vector> {

	private static Logger log = LoggerFactory.getLogger(ProfileFunction.class);

	private static final long serialVersionUID = 1L;
	private List<Dimension> dimensions;
	private ExternalNetwork network;

	protected ContactFunction(ExternalNetwork network, List<Dimension> dimensions) {
		this.dimensions = dimensions;
		this.network = network;
	}

	@Override
	public Vector call(Profile profile) {
		for(Contact contact : profile.getContacts()) {
			if(contact.getExternalIdentity().getExternalNetwork() == network.ordinal()) {
				double[] point = computePoint(contact, profile.getLocation(), dimensions);
				return Vectors.dense(point);
			}
		}
		return null;
	}

	/**
	 * Computes the N-dimensional position of this contact
	 * 
	 * @param contact
	 * @param dimensions
	 * 
	 * @return
	 */
	public static double[] computePoint(Contact contact, UserLocation userLocation, List<Dimension> dimensions) {

		double[] point = new double[4];

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

		dimension = Dimension.findDimensionByAttribute("lat", dimensions);
		point[2] = (userLocation.getLocation().getLatitude() == null || dimension == null) ? 0.0 : Dimension.computeCoordinates(userLocation.getLocation().getLatitude(), dimension);
		
		dimension = Dimension.findDimensionByAttribute("lon", dimensions);
		point[3] = (userLocation.getLocation().getLongitude() == null || dimension == null) ? 0.0 : Dimension.computeCoordinates(userLocation.getLocation().getLongitude(), dimension);

		log.info("points {}", point);

		return point;
	}
	
	

	private static Range<Integer> fill(AgeRange ageRange) {
		Integer min = ageRange.getMin() == null ? 0 : ageRange.getMin();
		Integer max = ageRange.getMax() == null ? Integer.MAX_VALUE : ageRange.getMax();
		return Range.between(min, max);
	}
}
