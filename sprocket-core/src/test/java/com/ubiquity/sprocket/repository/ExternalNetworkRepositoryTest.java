package com.ubiquity.sprocket.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.identity.domain.Application;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.Developer;
import com.ubiquity.identity.domain.ExternalNetworkApplication;
import com.ubiquity.identity.factory.TestApplicationFactory;
import com.ubiquity.identity.factory.TestDeveloperFactory;
import com.ubiquity.identity.factory.TestExternalApplicationFactory;
import com.ubiquity.identity.repository.ApplicationRepository;
import com.ubiquity.identity.repository.ApplicationRepositoryJpaImpl;
import com.ubiquity.identity.repository.DeveloperRepository;
import com.ubiquity.identity.repository.DeveloperRepositoryJpaImpl;
import com.ubiquity.identity.repository.ExternalNetworkApplicationRepository;
import com.ubiquity.identity.repository.ExternalNetworkApplicationRepositoryJpaImpl;
import com.ubiquity.integration.domain.ExternalNetwork;

public class ExternalNetworkRepositoryTest {
	private static Developer owner;
	private static Application application;
	private static ExternalNetworkApplication externalNetworkApplication1;
	private static ExternalNetworkApplication externalNetworkApplication2;
	private static ExternalNetworkApplicationRepository externalNetworkApplicationRepository;

	@After
	public void tearDown() throws Exception {
		EntityManagerSupport.closeEntityManager();
	}

	@Before
	public void setUp() throws Exception {
		ApplicationRepository applicationRepository = new ApplicationRepositoryJpaImpl();
		DeveloperRepository developerRepository = new DeveloperRepositoryJpaImpl();
		externalNetworkApplicationRepository = new ExternalNetworkApplicationRepositoryJpaImpl();
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

		Set<ClientPlatform> mobileClientplatforms = new HashSet<ClientPlatform>();
		mobileClientplatforms.add(ClientPlatform.Android);
		mobileClientplatforms.add(ClientPlatform.IOS);

		externalNetworkApplication1 = TestExternalApplicationFactory
				.createTestExternalNetworkApplicationWithMinimumRequiredProperties(
						application, mobileClientplatforms, ExternalNetwork
								.ordinalOrDefault(ExternalNetwork.Facebook));

		EntityManagerSupport.beginTransaction();
		externalNetworkApplicationRepository
				.create(externalNetworkApplication1);
		EntityManagerSupport.commit();

		Set<ClientPlatform> webClientplatform = new HashSet<ClientPlatform>();
		webClientplatform.add(ClientPlatform.WEB);
		externalNetworkApplication2 = TestExternalApplicationFactory
				.createTestExternalNetworkApplicationWithMinimumRequiredProperties(
						application, webClientplatform, ExternalNetwork
								.ordinalOrDefault(ExternalNetwork.Twitter));

		EntityManagerSupport.beginTransaction();
		externalNetworkApplicationRepository
				.create(externalNetworkApplication2);
		EntityManagerSupport.commit();
	}

	@Test
	public void testExists() {
		boolean exists = externalNetworkApplicationRepository.exists(
				externalNetworkApplication1.getConsumerKey(),
				externalNetworkApplication1.getConsumerSecret(),
				externalNetworkApplication1.getExternalNetwork());
		Assert.assertEquals(exists, Boolean.TRUE);

		exists = externalNetworkApplicationRepository.exists(UUID.randomUUID()
				.toString(), externalNetworkApplication1.getConsumerSecret(),
				externalNetworkApplication1.getExternalNetwork());
		Assert.assertEquals(exists, Boolean.FALSE);

		exists = externalNetworkApplicationRepository.exists(
				externalNetworkApplication1.getConsumerKey(), UUID.randomUUID()
						.toString(), externalNetworkApplication1
						.getExternalNetwork());
		Assert.assertEquals(exists, Boolean.FALSE);
	}

	@Test
	public void testFindAppsByDeveloperId() {
		List<ExternalNetworkApplication> externalApplications = externalNetworkApplicationRepository
				.getByApplicationId(application.getAppId());
		Assert.assertEquals(externalApplications.size(), 2);
	}

	@Test
	public void testGetByAppIdAndExternalNetworkAndClientPlatform() {
		ExternalNetworkApplication externalNetworkapp = externalNetworkApplicationRepository
				.getByAppIdAndExternalNetworkAndClientPlatform(
						application.getAppId(),
						externalNetworkApplication1.getExternalNetwork(),
						externalNetworkApplication1.getClientPlatforms().iterator().next());
		
		Assert.assertEquals(externalNetworkapp.getExternalApplicationId(), externalNetworkApplication1.getExternalApplicationId());
	}

}
