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
import com.ubiquity.location.domain.UserLocation;

public class ProfileFunction implements Function<Profile, Vector> {

	private static Logger log = LoggerFactory.getLogger(ProfileFunction.class);

	private static final long serialVersionUID = 1L;
	private List<Dimension> dimensions;

	protected ProfileFunction(List<Dimension> dimensions) {
		this.dimensions = dimensions;
	}

	@Override
	public Vector call(Profile profile) {

		double[] point = computePoint(profile, dimensions);
		
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
	
	public static double[] computePoint(Profile profile, List<Dimension> dimensions) {		
		
		
		double[] points = new double[2];
		UserLocation location = profile.getLocation();
		Dimension dimension = Dimension.findDimensionByAttribute("lat", dimensions);
		points[0] = (location.getLocation().getLatitude() == null || dimension == null) ? 0.0 : Dimension.computeCoordinates(location.getLocation().getLatitude(), dimension);
		
		dimension = Dimension.findDimensionByAttribute("lon", dimensions);
		points[1] = (location.getLocation().getLongitude() == null || dimension == null) ? 0.0 : Dimension.computeCoordinates(location.getLocation().getLongitude(), dimension);

		log.info("points {}", points);
		return points;				
	}
	
	
	

	private static Range<Integer> fill(AgeRange ageRange) {
		Integer min = ageRange.getMin() == null ? 0 : ageRange.getMin();
		Integer max = ageRange.getMax() == null ? Integer.MAX_VALUE : ageRange.getMax();
		return Range.between(min, max);
	}
}
