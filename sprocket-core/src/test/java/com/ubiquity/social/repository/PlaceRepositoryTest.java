package com.ubiquity.social.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
 * Tests testing basic CRUD operations for a user repository
 * 
 * @author chris
 * 
 */
public class PlaceRepositoryTest {

	private PlaceRepository placeRepository;

	private Place losAngeles, culverCity, culverHotel;

	private InterestRepository interestRepository;

	private List<Long> interests = new ArrayList<Long>();

	@After
	public void tearDown() throws Exception {
		EntityManagerSupport.closeEntityManager();
	}

	@Before
	public void setUp() throws Exception {

		
		interestRepository = new InterestRepositoryJpaImpl();

		Interest parentInterest = new Interest("Entertainment");
		Interest interest = new Interest("Music", parentInterest);
		parentInterest.addChild(interest);
		placeRepository = new PlaceRepositoryJpaImpl();

		losAngeles = new Place.Builder()
				.region("us")
				.lastUpdated(System.currentTimeMillis())
				.externalNetwork(ExternalNetwork.Yelp)
				.name("Los Angeles, CA")
				.boundingBox(
						new Geobox.Builder().center(
								new Location.Builder()
										.latitude(new BigDecimal(34.0536))
										.longitude(new BigDecimal(-118.2430))
										.build()).build()).build();

		culverCity = new Place.Builder()
				.region("us")
				.lastUpdated(System.currentTimeMillis())
				.externalNetwork(ExternalNetwork.Yelp)
				.name("Culver City, CA")
				.boundingBox(
						new Geobox.Builder()
								.center(new Location.Builder()
										.latitude(new BigDecimal(34.0211111))
										.longitude(new BigDecimal(-118.3961111))
										.build()).build()).build();

		culverHotel = new Place.Builder()
				.region("us")
				.lastUpdated(System.currentTimeMillis())
				.externalNetwork(ExternalNetwork.Yelp)
				.name("Culver Hotel")
				.boundingBox(
						new Geobox.Builder().center(
								new Location.Builder()
										.latitude(new BigDecimal(34.0238))
										.longitude(new BigDecimal(118.3943))
										.build()).build()).build();

		culverHotel.addInterest(interest);
		// add parent / child relationship
		culverCity.addChild(culverHotel);
		losAngeles.addChild(culverCity);

		// save tree

		try{
			EntityManagerSupport.beginTransaction();
			interestRepository.create(parentInterest);
			placeRepository.create(losAngeles);
			
			interests.add(interest.getInterestId());
		}
		catch (Exception ex)
		{
		}
		finally{
			EntityManagerSupport.commit();
		}

	}

	@Test
	public void testCreateParentAndChild() throws Exception {
		// find place by name
		Place place = placeRepository.findByName("Culver Hotel", null, "us");
		Assert.assertNotNull(place);
		Assert.assertNotNull(place.getParent());

		// make sure the parent is culver city
		Assert.assertEquals(place.getParent().getPlaceId(),
				culverCity.getPlaceId());
		// make sure grant parent is los angeles
		Assert.assertEquals(place.getParent().getParent().getPlaceId(),
				losAngeles.getPlaceId());

	}

	@Test
	public void testFindByName() throws Exception {
		Place place = placeRepository.findByName("Los Angeles, CA", ExternalNetwork.Yelp, "us");
		Assert.assertNotNull(place);
	}

	@Test(expected = PersistenceException.class)
	public void testCompositeIndex() throws Exception {
		Place duplicate = new Place.Builder()
				.boundingBox(losAngeles.getBoundingBox()).region("us")
				.name(losAngeles.getName())
				.externalNetwork(ExternalNetwork.Yelp)
				.lastUpdated(System.currentTimeMillis()).build();

		EntityManagerSupport.beginTransaction();
		placeRepository.create(duplicate);
		EntityManagerSupport.commit();
	}

	@Test
	public void testAllCitiesAndNeighborhoods() {
		List<Place> places = placeRepository.getAllCitiesAndNeighborhoods();
		Assert.assertNotNull(places);
		Assert.assertFalse(places.isEmpty());
		Assert.assertEquals(places.size(), 2);

	}

	@Test
	public void testAllNeighborhoods() {
		List<Place> places = placeRepository.getAllNeighborhoods();
		Assert.assertNotNull(places);
		Assert.assertFalse(places.isEmpty());
		Assert.assertEquals(places.size(), 1);
	}

	//@Test
	public void testfindPlacesByInterestIdAndProvider() {
		List<Place> places = placeRepository.findPlacesByInterestIdAndProvider(
				losAngeles.getPlaceId(), interests, ExternalNetwork.Yelp);
		Assert.assertNotNull(places);
		Assert.assertFalse(places.isEmpty());
		Assert.assertEquals(places.size(), 1);
	}

}
