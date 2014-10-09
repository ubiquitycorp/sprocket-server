package com.ubiquity.sprocket.analytics.recommendation;

import java.math.BigDecimal;
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
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.factory.TestUserFactory;
import com.ubiquity.identity.repository.UserRepository;
import com.ubiquity.identity.repository.UserRepositoryJpaImpl;
import com.ubiquity.integration.domain.AgeRange;
import com.ubiquity.integration.domain.Contact;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.Gender;
import com.ubiquity.integration.factory.TestContactFactory;
import com.ubiquity.location.domain.Location;
import com.ubiquity.location.domain.UserLocation;
import com.ubiquity.sprocket.domain.GroupMembership;

public class RecommendationEngineTest{

	private RecommendationEngine engine;

	@SuppressWarnings("unused")
	private Logger log = LoggerFactory.getLogger(getClass());

	private UserRepository userRepository;

	private Configuration config;

	@Before
	public void setUp() throws Exception {
		config = new PropertiesConfiguration("test.properties");
		engine = new RecommendationEngineSparkImpl(config);		

		// add dimension to global context with all weight values at 1
		engine.addDimension(Dimension.createFromEnum("gender", Gender.class, 0.0));
		engine.addDimension(new Dimension("ageRange", Range.between(0.0, 100.0), 0.0));
		engine.addDimension(new Dimension("lat", Range.between(-90.0, 90.0), 1.0)); // only location important
		engine.addDimension(new Dimension("lon", Range.between(-180.0, 180.0), 1.0));

		// create fb specific context, with dimensions where
		engine.addContext(ExternalNetwork.Facebook, config);
		engine.addDimension(Dimension.createFromEnum("gender", Gender.class, 0.1), ExternalNetwork.Facebook);
		engine.addDimension(new Dimension("ageRange", Range.between(0.0, 100.0), 1.0), ExternalNetwork.Facebook);
		engine.addDimension(new Dimension("lat", Range.between(-90.0, 90.0), 0.0)); // location we don't care about
		engine.addDimension(new Dimension("lon", Range.between(-180.0, 180.0), 0.0)); 
		

		userRepository = new UserRepositoryJpaImpl();
	}


	@Test
	public void testKMeansFindsNearestKClosestPoints() {

		List<Profile> profiles = new LinkedList<Profile>();

		User user = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		Contact.Builder contactBuilder = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(user, ExternalNetwork.Facebook);
		contactBuilder.gender(Gender.Male);
		contactBuilder.ageRange(new AgeRange(21, 35));
		persistUser(user);
		
		// build the profile with location
		Profile jack = new Profile(user, new UserLocation.Builder().location(
				new Location.Builder().latitude(new BigDecimal(34.0522300)).longitude(new BigDecimal(-118.2436800)).build()).user(user).build());
		jack.getContacts().add(contactBuilder.build());
		profiles.add(jack);

		user = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		contactBuilder = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(user, ExternalNetwork.Facebook);
		contactBuilder.gender(Gender.Male);
		contactBuilder.ageRange(new AgeRange(21, 35));
		persistUser(user);
		
		// build the profile with location
		Profile john = new Profile(user, new UserLocation.Builder().location(
				new Location.Builder().latitude(new BigDecimal(34.1234567)).longitude(new BigDecimal(-118.2412345)).build()).user(user).build());
		john.getContacts().add(contactBuilder.build());
		profiles.add(john);

		
		user = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		contactBuilder = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(user, ExternalNetwork.Facebook);
		contactBuilder.gender(Gender.Male);
		contactBuilder.ageRange(new AgeRange(55, 65));
		persistUser(user);
		
		// build the profile with location
		Profile joe = new Profile(user, new UserLocation.Builder().location(
				new Location.Builder().latitude(new BigDecimal(34.1334567)).longitude(new BigDecimal(-118.2499999)).build()).user(user).build());
		joe.getContacts().add(contactBuilder.build());
		profiles.add(joe);

		user = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		contactBuilder = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(user, ExternalNetwork.Facebook);
		contactBuilder.gender(Gender.Female);
		contactBuilder.ageRange(new AgeRange(55, 65));
		persistUser(user);
		
		Profile jill = new Profile(user, new UserLocation.Builder().location(
				new Location.Builder().latitude(new BigDecimal(-34.6131500)).longitude(new BigDecimal(-58.3772300)).build()).user(user).build());
		jill.getContacts().add(contactBuilder.build());
		profiles.add(jill);

		user = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		contactBuilder = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(user, ExternalNetwork.Facebook);
		contactBuilder.gender(Gender.Female);
		contactBuilder.ageRange(new AgeRange(55, 65));
		persistUser(user);
		
		Profile jane = new Profile(user, new UserLocation.Builder().location(
				new Location.Builder().latitude(new BigDecimal(-34.6231500)).longitude(new BigDecimal(-58.3872300)).build()).user(user).build());
		jane.getContacts().add(contactBuilder.build());
		profiles.add(jane);			
		
		engine.updateProfileRecords(profiles);
		
		
		
		engine.train(); // train global context
		
		// assign and persist
		GroupMembership johnMembership = engine.assign(john).get(0);
		GroupMembership joeMembership = engine.assign(joe).get(0);
		GroupMembership jackMembership = engine.assign(jack).get(0);
		GroupMembership jillMembership = engine.assign(jill).get(0);
		GroupMembership janeMembership = engine.assign(jane).get(0);

		
		// now test that the group classifications worked properly by location
		Assert.assertEquals(johnMembership.getGroupIdentifier(), jackMembership.getGroupIdentifier());
		Assert.assertEquals(joeMembership.getGroupIdentifier(), jackMembership.getGroupIdentifier());
		Assert.assertEquals(jillMembership.getGroupIdentifier(), janeMembership.getGroupIdentifier());
		
		// assert in the negative
		Assert.assertNotEquals(jillMembership.getGroupIdentifier(), jackMembership.getGroupIdentifier());
	
		
		// now classify a new user / contact without after training the model, from San Diego
		user = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		contactBuilder = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(user, ExternalNetwork.Facebook);
		contactBuilder.gender(Gender.Female);
		contactBuilder.ageRange(new AgeRange(21, 35));
		persistUser(user);
		
		Profile jenny = new Profile(user, new UserLocation.Builder().location(
				new Location.Builder().latitude(new BigDecimal(32.715786)).longitude(new BigDecimal(-117.158340)).build()).user(user).build());
		jenny.getContacts().add(contactBuilder.build());
		profiles.add(jenny);
		
		GroupMembership jennyMembership = engine.assign(jenny).get(0);
		
		// ensure that Jenny is grouped with jack, in LA not Argentina
		Assert.assertEquals(jennyMembership.getGroupIdentifier(), jackMembership.getGroupIdentifier());

		engine.train(ExternalNetwork.Facebook); // now train FB context

		// now their membership should be by age range
		johnMembership = engine.assign(john, ExternalNetwork.Facebook).get(0);
		joeMembership = engine.assign(joe, ExternalNetwork.Facebook).get(0);
		jackMembership = engine.assign(jack, ExternalNetwork.Facebook).get(0);
		jillMembership = engine.assign(jill, ExternalNetwork.Facebook).get(0);
		janeMembership = engine.assign(jane, ExternalNetwork.Facebook).get(0);
		jennyMembership = engine.assign(jenny, ExternalNetwork.Facebook).get(0);

		// now test that the group classifications worked properly by age range
		Assert.assertEquals(joeMembership.getGroupIdentifier(), jillMembership.getGroupIdentifier());
		Assert.assertEquals(joeMembership.getGroupIdentifier(), janeMembership.getGroupIdentifier());

		Assert.assertEquals(jennyMembership.getGroupIdentifier(), johnMembership.getGroupIdentifier());
		Assert.assertEquals(jennyMembership.getGroupIdentifier(), jackMembership.getGroupIdentifier());
		
		// test a negative assertion (ensuring the is more than 1 cluster)
		Assert.assertNotEquals(joeMembership.getGroupIdentifier(), jennyMembership.getGroupIdentifier());
		
		// test an assignment back to global context is not the same as from fb, and that the models are in tact
		GroupMembership jennyGlobal = engine.assign(jenny).get(0);
		Assert.assertNotEquals(jennyGlobal.getExternalNetwork(), jennyMembership.getExternalNetwork());


	}



	private void persistUser(User user) {
		EntityManagerSupport.beginTransaction();
		userRepository.create(user);
		EntityManagerSupport.commit();
	}



}
