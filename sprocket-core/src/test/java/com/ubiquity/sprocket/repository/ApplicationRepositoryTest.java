package com.ubiquity.sprocket.repository;

import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.identity.domain.Application;
import com.ubiquity.identity.domain.Developer;
import com.ubiquity.identity.factory.TestApplicationFactory;
import com.ubiquity.identity.factory.TestDeveloperFactory;
import com.ubiquity.identity.repository.ApplicationRepository;
import com.ubiquity.identity.repository.ApplicationRepositoryJpaImpl;
import com.ubiquity.identity.repository.DeveloperRepository;
import com.ubiquity.identity.repository.DeveloperRepositoryJpaImpl;

/**
 * Tests basic CRUD operations for application repository
 * @author shimaa
 *
 */
public class ApplicationRepositoryTest {

	private static Developer owner;
	private static Application application;
	private static DeveloperRepository developerRepository;
	private static ApplicationRepository applicationRepository;

	@After
	public void tearDown() throws Exception {
		EntityManagerSupport.closeEntityManager();
	}

	@Before
	public void setUp() throws Exception {
		applicationRepository = new ApplicationRepositoryJpaImpl();
		developerRepository = new DeveloperRepositoryJpaImpl();

		owner = TestDeveloperFactory
				.createTestDeveloperWithMinimumRequiredProperties();

		EntityManagerSupport.beginTransaction();
		developerRepository.create(owner);
		EntityManagerSupport.commit();

		application = TestApplicationFactory
				.createTestApplicationWithMinimumRequiredProperties(owner);

		EntityManagerSupport.beginTransaction();
		applicationRepository.create(application);
		EntityManagerSupport.commit();
	}

	@Test
	public void testExists() {
		boolean exists = applicationRepository.exists(application.getAppKey(),
				application.getAppSecret());
		Assert.assertEquals(exists, Boolean.TRUE);
		exists = applicationRepository.exists(UUID.randomUUID().toString(),
				application.getAppSecret());
		Assert.assertEquals(exists, Boolean.FALSE);
		exists = applicationRepository.exists(application.getAppKey(), UUID
				.randomUUID().toString());
		Assert.assertEquals(exists, Boolean.FALSE);
	}

	@Test
	public void testFindAppsByDeveloperId() {
		List<Application> developerApplications = applicationRepository
				.findAppsByDeveloperId(owner.getDeveloperId());
		Assert.assertEquals(developerApplications.size(), 1);
	}

	@Test
	public void testFindByAppKeyAndAppSecret() {
		Application testApplication = applicationRepository
				.findByAppKeyAndAppSecret(application.getAppKey(),
						application.getAppSecret());
		Assert.assertNotEquals(testApplication, null);
		testApplication = applicationRepository.findByAppKeyAndAppSecret(UUID
				.randomUUID().toString(), application.getAppSecret());
		Assert.assertEquals(testApplication, null);
	}

	@Test
	public void testdeactivateApp() {
		try {
			EntityManagerSupport.beginTransaction();
			boolean deactivated = applicationRepository
					.deactivateApp(application.getAppId());
			EntityManagerSupport.commit();
			Assert.assertEquals(deactivated, Boolean.TRUE);
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
		
		Application testApplication = applicationRepository
				.findByAppKeyAndAppSecret(application.getAppKey(),
						application.getAppSecret());
		Assert.assertEquals(testApplication.isActive(), Boolean.FALSE);
	}

	@Test
	public void testDeleteAppsByDeveloperId() {
		EntityManagerSupport.beginTransaction();
		boolean deleted = applicationRepository.deleteAppsByDeveloperId(owner
				.getDeveloperId());
		EntityManagerSupport.commit();
		Assert.assertEquals(deleted, Boolean.TRUE);
		
		Application testApplication = applicationRepository
				.findByAppKeyAndAppSecret(application.getAppKey(),
						application.getAppSecret());
		Assert.assertEquals(testApplication, null);
	}
}
