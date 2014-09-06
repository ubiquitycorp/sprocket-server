package com.ubiquity.sprocket.repository;

import java.math.BigDecimal;
import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.PersistenceException;
import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.sprocket.domain.Geobox;
import com.ubiquity.sprocket.domain.Location;
import com.ubiquity.sprocket.domain.Place;

/***
 * Tests testing basic CRUD operations for a location repository
 * 
 * @author chris
 *
 */
public class PlaceRepositoryTest {

	private Logger log = LoggerFactory.getLogger(getClass());

	private PlaceRepository placeRepository;
	
	private Place place;
	
	
	@After
	public void tearDown() throws Exception {
		EntityManagerSupport.closeEntityManager();
	}

	@Before
	public void setUp() throws Exception {

		placeRepository = new PlaceRepositoryJpaImpl();

		place = new Place.Builder().locale(Locale.US).name("Los Angeles").boundingBox(
				new Geobox.Builder()
						.center(
								new Location.Builder().latitude(new BigDecimal(59.93939393)).longitude(new BigDecimal(-34.3030303)).build())
						.lowerLeft(
								new Location.Builder().latitude(new BigDecimal(59.93939393)).longitude(new BigDecimal(-34.3030303)).build())
						.lowerRight(
								new Location.Builder().latitude(new BigDecimal(59.93939393)).longitude(new BigDecimal(-34.3030303)).build())
						.upperLeft(
								new Location.Builder().latitude(new BigDecimal(59.93939393)).longitude(new BigDecimal(-34.3030303)).build())
						.upperRight(
								new Location.Builder().latitude(new BigDecimal(59.93939393)).longitude(new BigDecimal(-34.3030303)).build())
								.build())
							.build();
		
		
		EntityManagerSupport.beginTransaction();
		placeRepository.create(place);		
		EntityManagerSupport.commit();
		
		log.info("id {}", place.getPlaceId());
	}

	@Test
	public void testFindByName() throws Exception {
		Place place = placeRepository.findByName("Los Angeles", Locale.US);
		Assert.assertNotNull(place);
	}
	
	@Test(expected = PersistenceException.class)
	public void testCompositeIndex() throws Exception {
		Place duplicate = new Place.Builder().boundingBox(place.getBoundingBox()).locale(place.getLocale()).name(place.getName()).build();
		
		EntityManagerSupport.beginTransaction();
		placeRepository.create(duplicate);
		EntityManagerSupport.commit();
	}
	
	

	

}
