package com.ubiquity.sprocket.analytics.recommendation;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.Range;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.factory.TestUserFactory;
import com.ubiquity.identity.repository.UserRepository;
import com.ubiquity.identity.repository.UserRepositoryJpaImpl;
import com.ubiquity.integration.factory.TestContactFactory;
import com.ubiquity.social.domain.AgeRange;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Gender;
import com.ubiquity.sprocket.domain.GroupMembership;

public class RecommendationEngineTest{

	private RecommendationEngine engine;

	private Logger log = LoggerFactory.getLogger(getClass());

	private GroupMembership johnMembership, jackMembership, joeMembership, jillMembership, janeMembership, jennyMembership;
	private GroupMembership johnFbMembership, jackFbMembership, joeFbMembership, jillFbMembership, janeFbMembership, jennyFbMembership;
	private UserRepository userRepository;

	private Configuration config;

	private String fbContext = ExternalNetwork.Facebook.toString();
	@Before
	public void setUp() throws Exception {
		config = new PropertiesConfiguration("test.properties");
		engine = new RecommendationEngineSparkImpl(config);		

		// add dimension to global context with all weight values at 1
		engine.addDimension(Dimension.createFromEnum("gender", Gender.class));
		engine.addDimension(new Dimension("ageRange", Range.between(0.0, 100.0), 1.0));

		// create fb specific context, with dimensions where
		engine.addContext(fbContext, config);
		engine.addDimension(Dimension.createFromEnum("gender", Gender.class, 0.1), fbContext);
		engine.addDimension(new Dimension("ageRange", Range.between(0.0, 100.0), 1.0), fbContext);

		userRepository = new UserRepositoryJpaImpl();
	}


	@Test
	public void testKMeansFindsNearestKClosestPoints() {

		List<Contact> contacts = new LinkedList<Contact>();

		User jack = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		Contact.Builder contactBuilder = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(jack, ExternalNetwork.Facebook);
		contactBuilder.gender(Gender.Male);
		contactBuilder.ageRange(new AgeRange(21, 35));
		persistUser(jack);
		contacts.add(contactBuilder.build());

		User john = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		contactBuilder = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(john, ExternalNetwork.Facebook);
		contactBuilder.gender(Gender.Male);
		contactBuilder.ageRange(new AgeRange(21, 35));
		persistUser(john);
		contacts.add(contactBuilder.build());

		User joe = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		contactBuilder = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(joe, ExternalNetwork.Facebook);
		contactBuilder.gender(Gender.Male);
		contactBuilder.ageRange(new AgeRange(55, 65));

		persistUser(joe);
		contacts.add(contactBuilder.build());

		User jill = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		contactBuilder = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(jill, ExternalNetwork.Facebook);
		contactBuilder.gender(Gender.Female);
		contactBuilder.ageRange(new AgeRange(55, 65));
		persistUser(jill);
		contacts.add(contactBuilder.build());

		User jane = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		contactBuilder = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(jane, ExternalNetwork.Facebook);
		contactBuilder.gender(Gender.Female);
		contactBuilder.ageRange(new AgeRange(55, 65));
		persistUser(jane);
		contacts.add(contactBuilder.build());

		engine.updateProfileRecords(contacts);
		// running train will train the model (create the clusters and centroids) and also classify the existing Contacts used in the
		// computation
		engine.train(); // train global context
		
		// assign and persist
		List<GroupMembership> membershipList = engine.assign(contacts);
		for(GroupMembership membership : membershipList) {
			// only evaluate global
			if(membership.getExternalNetwork() != null)
				continue;
			if(membership.getUser().getUserId().equals(john.getUserId())) {
				johnMembership = membership;
			} else if(membership.getUser().getUserId().equals(jack.getUserId())) {
				jackMembership = membership;
			} else if(membership.getUser().getUserId().equals(joe.getUserId())) {
				joeMembership = membership;
			} else if(membership.getUser().getUserId().equals(jane.getUserId())) {
				janeMembership = membership;
			} else if(membership.getUser().getUserId().equals(jill.getUserId())) {
				jillMembership = membership;
			} else {
				Assert.assertTrue("Membership not mapped to a valid test user", Boolean.FALSE);
			}
		}

		// now test that the group classifications worked properly by gender
		Assert.assertEquals(johnMembership.getGroupIdentifier(), jackMembership.getGroupIdentifier());
		Assert.assertEquals(joeMembership.getGroupIdentifier(), jackMembership.getGroupIdentifier());
		Assert.assertEquals(jillMembership.getGroupIdentifier(), janeMembership.getGroupIdentifier());

		
		// now classify a new user / contact without after training the model
		User jenny = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		contactBuilder = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(jenny, ExternalNetwork.Facebook);
		contactBuilder.gender(Gender.Female);
		contactBuilder.ageRange(new AgeRange(21, 35));
		persistUser(jenny);

		// adding new contacts also tests the union of the 2 data sets 
		List<Contact> newContacts = Arrays.asList(new Contact[] { contactBuilder.build() });
		engine.updateProfileRecords(newContacts);

		// assign in the global context, where gender matters most
		List<GroupMembership> membership = engine.assign(newContacts);

		// verify that the model is working...jenny should clustered with the girls
		jennyMembership = membership.get(0);
		Assert.assertEquals(jennyMembership.getGroupIdentifier(), jillMembership.getGroupIdentifier());


		engine.train(fbContext); // now train fb context
		
		// now add new contacts (Jenny) into a single array and let's classify them
		contacts.addAll(newContacts);
		
		log.info("Assigning FB groups...");
		// assign in the FB context, where 
		membershipList = engine.assign(contacts, fbContext);
		for(GroupMembership fbMembership : membershipList) {
			// only evaluate fb
			if(fbMembership.getExternalNetwork() != ExternalNetwork.Facebook)
				continue;

			if(fbMembership.getUser().getUserId().equals(john.getUserId())) {
				johnFbMembership = fbMembership;
			} else if(fbMembership.getUser().getUserId().equals(jack.getUserId())) {
				jackFbMembership = fbMembership;
			} else if(fbMembership.getUser().getUserId().equals(joe.getUserId())) {
				joeFbMembership = fbMembership;
			} else if(fbMembership.getUser().getUserId().equals(jane.getUserId())) {
				janeFbMembership = fbMembership;
			} else if(fbMembership.getUser().getUserId().equals(jill.getUserId())) {
				jillFbMembership = fbMembership;
			} else if(fbMembership.getUser().getUserId().equals(jenny.getUserId())) {
				jennyFbMembership = fbMembership;
			} else {
				Assert.assertTrue("Membership not mapped to a valid test user", Boolean.FALSE);
			}
		}

		// now test that the group classifications worked properly by age range
		Assert.assertEquals(joeFbMembership.getGroupIdentifier(), jillFbMembership.getGroupIdentifier());
		Assert.assertEquals(joeFbMembership.getGroupIdentifier(), janeFbMembership.getGroupIdentifier());

		Assert.assertEquals(jennyFbMembership.getGroupIdentifier(), johnFbMembership.getGroupIdentifier());
		Assert.assertEquals(jennyFbMembership.getGroupIdentifier(), jackFbMembership.getGroupIdentifier());


		

	}


	private void persistUser(User user) {
		EntityManagerSupport.beginTransaction();
		userRepository.create(user);
		EntityManagerSupport.commit();
	}



}
