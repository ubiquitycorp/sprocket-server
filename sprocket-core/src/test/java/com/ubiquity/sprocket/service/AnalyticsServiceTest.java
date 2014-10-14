package com.ubiquity.sprocket.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.CollectionVariant;
import com.ubiquity.identity.service.UserService;
import com.ubiquity.integration.domain.Activity;
import com.ubiquity.integration.domain.AgeRange;
import com.ubiquity.integration.domain.Contact;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.Gender;
import com.ubiquity.integration.service.ContactService;
import com.ubiquity.sprocket.analytics.recommendation.Profile;
import com.ubiquity.sprocket.analytics.recommendation.factory.TestEngagedActivityFactory;
import com.ubiquity.sprocket.analytics.recommendation.factory.TestProfileFactory;
import com.ubiquity.sprocket.domain.EngagedActivity;
import com.ubiquity.sprocket.domain.GroupMembership;
import com.ubiquity.sprocket.repository.GroupMembershipRepository;
import com.ubiquity.sprocket.repository.GroupMembershipRepositoryJpaImpl;

public class AnalyticsServiceTest {

	private static AnalyticsService analyticsService;
	private static UserService userService;
	private static ContactService contactService;
	
	private static GroupMembershipRepository groupMembershipRepository;

	@SuppressWarnings("unused")
	private Logger log = LoggerFactory.getLogger(getClass());

	private static Map<String, Profile> profileMap;

	@BeforeClass
	public static void setUp() throws Exception {
		Configuration config = new PropertiesConfiguration("test.properties");
		analyticsService = new AnalyticsService(config);
		userService = new UserService(config);
		contactService = new ContactService(config);
		
		groupMembershipRepository = new GroupMembershipRepositoryJpaImpl();

		profileMap = TestProfileFactory.createProfilesForRecommendationTest();

		// persist user and contact records
		for(Profile profile : profileMap.values()) {
			persist(profile);
		}
	}

	@Test
	public void testRecommendActivities() {


		// add engaged content that john and jack are looking at
		EngagedActivity engagedActivity = TestEngagedActivityFactory.createEngagedActivity(profileMap.get("john").getUser(), ExternalNetwork.Facebook);		
		analyticsService.track(engagedActivity);

		analyticsService.refreshProfileRecords();
		analyticsService.assignGroupsAndCreateRecommendedContent();

		GroupMembership johnMembership = groupMembershipRepository.findAllByUserId(profileMap.get("john").getUser().getUserId()).get(0);
		GroupMembership jackMembership = groupMembershipRepository.findAllByUserId(profileMap.get("jack").getUser().getUserId()).get(0);
		GroupMembership jillMembership = groupMembershipRepository.findAllByUserId(profileMap.get("jill").getUser().getUserId()).get(0);

		// do a quick membership test
		Assert.assertEquals(johnMembership.getGroupIdentifier(), jackMembership.getGroupIdentifier());
		// assert in the negative
		Assert.assertNotEquals(jillMembership.getGroupIdentifier(), jackMembership.getGroupIdentifier());

		CollectionVariant<Activity> variant = analyticsService.findAllRecommendedActivities(profileMap.get("john").getUser().getUserId(), ExternalNetwork.Facebook, 1l);
		Assert.assertNotNull(variant);
		Assert.assertTrue(!variant.getCollection().isEmpty());
		
		// new user jenny comes along that will go in jill's group
		Profile jenny = TestProfileFactory.createProfile(ExternalNetwork.Facebook, Gender.Female, new AgeRange(55, 65), 34.00000, -118.0000);
		persist(jenny);
		
		// assign via FB
		analyticsService.assign(jenny.getContacts().get(0));
		
		GroupMembership jennyMembership = groupMembershipRepository.findAllByUserId(jenny.getUser().getUserId()).get(0);
		Assert.assertEquals(jennyMembership.getGroupIdentifier(), jillMembership.getGroupIdentifier());

		
		// now jenny user should not get anything because nobody in this group has engaged with anything; we only have assignments
		variant = analyticsService.findAllRecommendedActivities(jenny.getUser().getUserId(), ExternalNetwork.Facebook, 1l);
		Assert.assertNotNull(variant);
		Assert.assertTrue(variant.getCollection().isEmpty());
		
	
		
		

	}
	
	
	private static void persist(Profile profile) {
		userService.create(profile.getUser());
		List<Contact> contacts = profile.getContacts();
		for(Contact contact : contacts) {
			contactService.update(contact);
		}
	}

}
