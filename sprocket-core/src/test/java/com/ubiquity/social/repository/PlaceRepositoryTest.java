package com.ubiquity.social.repository;

import java.math.BigDecimal;
import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.location.domain.Geobox;
import com.ubiquity.location.domain.Location;
import com.ubiquity.location.domain.Place;
import com.ubiquity.location.repository.PlaceRepository;
import com.ubiquity.location.repository.PlaceRepositoryJpaImpl;


/***
 * Tests testing basic CRUD operations for a user repository
 * 
 * @author chris
 *
 */
public class PlaceRepositoryTest {

	private PlaceRepository placeRepository;

	private Place losAngeles, culverCity, culverHotel;

	@After
	public void tearDown() throws Exception {
		EntityManagerSupport.closeEntityManager();
	}

	@Before
	public void setUp() throws Exception {

		placeRepository = new PlaceRepositoryJpaImpl();

		losAngeles = new Place.Builder()
		.locale(Locale.US)
		.name("Los Angeles, CA").boundingBox(new Geobox.Builder()
		.center(new Location.Builder()
		.latitude(new BigDecimal(34.0536))
		.longitude(new BigDecimal(-118.2430))
		.build())
		.build())
		.build();

		culverCity = new Place.Builder()
		.locale(Locale.US)
		.name("Culver City, CA").boundingBox(new Geobox.Builder()
		.center(new Location.Builder()
		.latitude(new BigDecimal(34.0211111))
		.longitude(new BigDecimal(-118.3961111))
		.build())
		.build())
		.build();

		culverHotel = new Place.Builder()
		.locale(Locale.US)
		.name("Culver Hotel").boundingBox(new Geobox.Builder()
		.center(new Location.Builder()
		.latitude(new BigDecimal(34.0238))
		.longitude(new BigDecimal(118.3943))
		.build())
		.build())
		.build();

		// add parent / child relationship
		culverCity.addChild(culverHotel);
		losAngeles.addChild(culverCity);

		// save tree
		EntityManagerSupport.beginTransaction();
		placeRepository.create(losAngeles);
		EntityManagerSupport.commit();


	}

	@Test
	public void testCreateParentAndChild() throws Exception {
		// find place by name
		Place place = placeRepository.findByName("Culver Hotel", Locale.US);
		Assert.assertNotNull(place);
		Assert.assertNotNull(place.getParent());
		
		// make sure the parent is culver city
		Assert.assertEquals(place.getParent().getPlaceId(), culverCity.getPlaceId());
		// make sure grant parent is los angeles
		Assert.assertEquals(place.getParent().getParent().getPlaceId(), losAngeles.getPlaceId());

	}
	
	



}
