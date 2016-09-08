package com.ubiquity.sprocket.repository;

import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.identity.domain.Developer;
import com.ubiquity.identity.factory.TestDeveloperFactory;
import com.ubiquity.identity.repository.DeveloperRepository;
import com.ubiquity.identity.repository.DeveloperRepositoryJpaImpl;

/**
 * Tests basic CRUD operations for developer repository
 * @author shimaa
 *
 */
public class DeveloperRepositoryTest {

	private static Developer developer;
	private static DeveloperRepository developerRepository;

	@After
	public void tearDown() {
		EntityManagerSupport.closeEntityManager();
	}

	@Before
	public void setUp() {

		developerRepository = new DeveloperRepositoryJpaImpl();

		developer = TestDeveloperFactory
				.createTestDeveloperWithMinimumRequiredProperties();
		EntityManagerSupport.beginTransaction();
		developerRepository.create(developer);
		EntityManagerSupport.commit();

	}

	@Test
	public void testExists() {
		boolean exists = developerRepository.exists(developer.getDeveloperId());
		Assert.assertEquals(exists, Boolean.TRUE);
		exists = developerRepository.exists(100L);
		Assert.assertEquals(exists, Boolean.FALSE);
	}

	@Test
	public void testCountAllActiveDevelopers() {
		Long count = developerRepository.countAllActiveDevelopers();
		assert (count == 1);
	}

	@Test
	public void testFindAll() {
		List<Developer> developers = developerRepository.findAll();
		Assert.assertEquals(developers.size(), 1);
	}

	@Test
	public void testFindAllActiveDevelopers() {
		List<Developer> developers = developerRepository
				.findAllActiveDevelopers();
		assert (developers.size() == 1);
	}

	@Test
	public void testSearchDeveloperByUsernameAndPassword() {
		Developer testDeveloper = developerRepository
				.searchDeveloperByUsernameAndPassword(developer.getUsername(),
						developer.getPassword());
		Assert.assertNotEquals(testDeveloper, null);

		testDeveloper = developerRepository
				.searchDeveloperByUsernameAndPassword(UUID.randomUUID()
						.toString(), developer.getPassword());
		Assert.assertEquals(testDeveloper, null);

	}
	
	@Test
	public void testFindByEmail() {
		Developer testDeveloper = developerRepository
				.findByEmail(developer.getEmail());
		Assert.assertNotEquals(testDeveloper, null);
		testDeveloper = developerRepository
				.findByEmail(UUID.randomUUID().toString());
		Assert.assertEquals(testDeveloper, null);
	}
	
	@Test
	public void testGetDeveloperById()
	{
		Developer testDeveloper = developerRepository.getDeveloperById(developer.getDeveloperId());
		Assert.assertNotEquals(testDeveloper, null);
		testDeveloper = developerRepository.getDeveloperById(100L);
		Assert.assertEquals(testDeveloper, null);
	}

	@Test
	public void testDeleteAll() {
		EntityManagerSupport.beginTransaction();
		developerRepository.deleteAll();
		EntityManagerSupport.commit();
		Developer testDeveloper = developerRepository.getDeveloperById(developer.getDeveloperId());
		Assert.assertEquals(testDeveloper, null);
		Long count = developerRepository.countAllActiveDevelopers();
		assert (count == 0);
	}
}
