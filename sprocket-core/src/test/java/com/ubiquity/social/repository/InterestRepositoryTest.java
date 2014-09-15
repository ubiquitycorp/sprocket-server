package com.ubiquity.social.repository;

import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.sprocket.domain.ExternalInterest;
import com.ubiquity.sprocket.domain.Interest;
import com.ubiquity.sprocket.repository.ExternalInterestRepository;
import com.ubiquity.sprocket.repository.ExternalInterestRepositoryJpaImpl;
import com.ubiquity.sprocket.repository.InterestRepository;
import com.ubiquity.sprocket.repository.InterestRepositoryJpaImpl;

/***
 * Tests testing basic CRUD operations for a user repository
 * 
 * @author chris
 *
 */
public class InterestRepositoryTest {

	private InterestRepository interestRepository;
	private ExternalInterestRepository externalInterestRepository;
	
	private Interest interest;
	
	@After
	public void tearDown() throws Exception {
		EntityManagerSupport.closeEntityManager();
	}

	@Before
	public void setUp() throws Exception {

		interestRepository = new InterestRepositoryJpaImpl();
		externalInterestRepository = new ExternalInterestRepositoryJpaImpl();

		interest = new Interest(UUID.randomUUID().toString(), null);
		interest.addChild(new Interest(UUID.randomUUID().toString()));
		
		EntityManagerSupport.beginTransaction();
		interestRepository.create(interest);
		EntityManagerSupport.commit();

	}

	@Test
	public void testCreateParentAndChild() throws Exception {
		Assert.assertNotNull(interest.getInterestId());
		Assert.assertTrue(!interest.getChildren().isEmpty());
	}
	
	@Test
	public void testCreateExternal() {
		ExternalInterest external = new ExternalInterest(UUID.randomUUID().toString(), interest, ExternalNetwork.Facebook);
		EntityManagerSupport.beginTransaction();
		externalInterestRepository.create(external);
		EntityManagerSupport.commit();
	}

}
