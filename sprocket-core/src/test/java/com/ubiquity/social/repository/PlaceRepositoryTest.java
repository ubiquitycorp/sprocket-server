package com.ubiquity.social.repository;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.Interest;
import com.ubiquity.integration.factory.TestPlaceFactory;
import com.ubiquity.integration.repository.InterestRepository;
import com.ubiquity.integration.repository.InterestRepositoryJpaImpl;
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

	private InterestRepository interestRepository;

	private List<Long> interests = new ArrayList<Long>();

	@After
	public void tearDown() throws Exception {

		try {
			EntityManagerSupport.beginTransaction();
			placeRepository.delete(losAngeles);
			EntityManagerSupport.commit();
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

	@Before
	public void setUp() throws Exception {

		
		interestRepository = new InterestRepositoryJpaImpl();

		Interest parentInterest = new Interest("Entertainment");
		Interest interest = new Interest("Music", parentInterest);
		parentInterest.addChild(interest);
		placeRepository = new PlaceRepositoryJpaImpl();

		losAngeles = TestPlaceFactory.createLosAngelesAndNeighborhoodsAndBusiness();

		// now set the parents for easier testing
		culverCity = losAngeles.getChildren().iterator().next();
		culverHotel = culverCity.getChildren().iterator().next();
		
		culverHotel.addInterest(interest);
	
		try {
			EntityManagerSupport.beginTransaction();
			interestRepository.create(parentInterest);
			placeRepository.create(losAngeles);
			interests.add(interest.getInterestId());
		} finally {
			EntityManagerSupport.commit();
		}
	}

	@Test
	public void testGetByExternalIdentifierAndNetwork() throws Exception {
		Place place = placeRepository.getByExernalIdentifierAndNetwork(culverHotel.getExternalIdentifier(), ExternalNetwork.Yelp);
		Assert.assertNotNull(place);  

		// make sure network filter is working with defaults
		place = placeRepository.getByExernalIdentifierAndNetwork(culverHotel.getExternalIdentifier(), null);
		Assert.assertNull(place);  
	}

	@Test
	public void testCreateParentAndChild() throws Exception {
		// find place by name
		Place place = placeRepository.getByLocatorAndExternalNetwork(culverHotel.getLocator(), ExternalNetwork.Yelp);
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
	public void testGetByLocator() throws Exception {
		Place place = placeRepository.getByLocatorAndExternalNetwork(losAngeles.getLocator(), null);
		Assert.assertNotNull(place);
	}

	@Test(expected = PersistenceException.class)
	@Ignore
	public void testCompositeIndex() throws Exception {
		Place duplicate = new Place.Builder().boundingBox(losAngeles.getBoundingBox()).region("us").name(losAngeles.getName()).build();

		try {
			EntityManagerSupport.beginTransaction();
			placeRepository.create(duplicate);
			EntityManagerSupport.commit();
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
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
