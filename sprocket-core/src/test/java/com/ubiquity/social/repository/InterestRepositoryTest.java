package com.ubiquity.social.repository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.integration.domain.ExternalInterest;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.Interest;
import com.ubiquity.integration.repository.ExternalInterestRepository;
import com.ubiquity.integration.repository.ExternalInterestRepositoryJpaImpl;
import com.ubiquity.integration.repository.InterestRepository;
import com.ubiquity.integration.repository.InterestRepositoryJpaImpl;


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
	private ExternalInterest external;
	
	private String tagFromExternalNetwork = UUID.randomUUID().toString();
	
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
		
		external = new ExternalInterest(tagFromExternalNetwork, interest, ExternalNetwork.Facebook);
		EntityManagerSupport.beginTransaction();
		externalInterestRepository.create(external);
		EntityManagerSupport.commit();
	}

	@Test
	public void testCreateParentAndChild() throws Exception {
		Assert.assertNotNull(interest.getInterestId());
		Assert.assertTrue(!interest.getChildren().isEmpty());
	}
	
	@Test
	public void testCreateExternal() {
		Assert.assertNotNull(external.getExternalInterestId());
	}
	
	@Test
	public void testFindExternalByNameAndExternalNetwork() {
		ExternalInterest persisted = externalInterestRepository.getByNameAndExternalNetworkAndInterestId(tagFromExternalNetwork, ExternalNetwork.Facebook ,interest.getInterestId());
		Assert.assertNotNull(persisted);
		
		persisted = externalInterestRepository.getByNameAndExternalNetworkAndInterestId(tagFromExternalNetwork, ExternalNetwork.YouTube,interest.getInterestId());
		Assert.assertNull(persisted);
	}
	
	@Test
	public void testFindExternalByNamesAndExternalNetwork() {
		List<ExternalInterest> persisted = externalInterestRepository.findByNamesAndExternalNetwork(new HashSet<String>(Arrays.asList(new String[] { tagFromExternalNetwork })), ExternalNetwork.Facebook);
		Assert.assertFalse(persisted.isEmpty());
	}
	

}
