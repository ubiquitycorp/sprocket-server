package com.ubiquity.sprocket.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.PersistenceException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.external.repository.InterestRepository;
import com.ubiquity.external.repository.InterestRepositoryJpaImpl;
import com.ubiquity.location.domain.Geobox;
import com.ubiquity.location.domain.Location;
import com.ubiquity.location.domain.Place;
import com.ubiquity.location.repository.PlaceRepository;
import com.ubiquity.location.repository.PlaceRepositoryJpaImpl;
import com.ubiquity.social.domain.Interest;

/***
 * Tests testing basic CRUD operations for a location repository
 * 
 * @author chris
 *
 */
public class PlaceRepositoryTest {

	private Logger log = LoggerFactory.getLogger(getClass());

	private PlaceRepository placeRepository;
	private InterestRepository interestRepository;
	private Place city;
	private Place neighborhood;
	private Place place;
	private List<Long> interests;
	
	
	@After
	public void tearDown() throws Exception {
		EntityManagerSupport.closeEntityManager();
	}

	@Before
	public void setUp() throws Exception {
		interests = new ArrayList<Long>();
		placeRepository = new PlaceRepositoryJpaImpl();
		interestRepository = new InterestRepositoryJpaImpl();
		Interest parentInterest = new Interest("Entertainment");
		Interest interest = new Interest("Music",parentInterest);
		city = new Place.Builder().locale(Locale.US).name("Los Angeles").boundingBox(
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
							.lastUpdated(System.currentTimeMillis())
							.build();
		
		neighborhood = new Place.Builder().locale(Locale.US).name("Adams Normandie").boundingBox(
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
							.parent(city)
							.lastUpdated(System.currentTimeMillis())
							.build();
		place = new Place.Builder().locale(Locale.US).name("Test Resturnat").boundingBox(
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
							.parent(neighborhood)
							.lastUpdated(System.currentTimeMillis())
							.build();
		try
		{
			EntityManagerSupport.beginTransaction();
			interestRepository.create(parentInterest);
			interestRepository.create(interest);
			interests.add(parentInterest.getInterestId());
			interests.add(interest.getInterestId());
			place.getInterests().add(interest);
			placeRepository.create(city);
			placeRepository.create(neighborhood);
			placeRepository.create(place);
		}catch (Exception ex )
		{ 
			
		}
		finally
		{
			EntityManagerSupport.commit();
		}
		
		log.info("id {}", place.getPlaceId());
	}

	@Test
	public void testFindByName() throws Exception {
		Place place = placeRepository.findByName("Los Angeles", Locale.US);
		Assert.assertNotNull(place);
	}
	
	
	@Test(expected = PersistenceException.class)
	public void testCompositeIndex() throws Exception {
		Place duplicate = new Place.Builder().boundingBox(place.getBoundingBox()).locale(place.getLocale()).name(place.getName()).lastUpdated(System.currentTimeMillis()).build();
		
		EntityManagerSupport.beginTransaction();
		placeRepository.create(duplicate);
		EntityManagerSupport.commit();
	}
	
	@Test
	public void testAllCitiesAndNeighborhoods() {
		List<Place> places =  placeRepository.getAllCitiesAndNeighborhoods();
		Assert.assertNotNull(places);
		Assert.assertFalse(places.isEmpty());
		Assert.assertEquals(places.size(), 2);

	}

	@Test
	public void testAllNeighborhoods() {
		List<Place> places =  placeRepository.getAllNeighborhoods();
		Assert.assertNotNull(places);
		Assert.assertFalse(places.isEmpty());
		Assert.assertEquals(places.size(), 1);
	}
	
	@Test
	public void testfindPlacesByInterestIdAndProvider() {
		List<Place> places =  placeRepository.findPlacesByInterestIdAndProvider(place.getPlaceId(),interests,ExternalNetwork.Yelp);
		Assert.assertNotNull(places);
		Assert.assertFalse(places.isEmpty());
		Assert.assertEquals(places.size(), 1);
	}

}
