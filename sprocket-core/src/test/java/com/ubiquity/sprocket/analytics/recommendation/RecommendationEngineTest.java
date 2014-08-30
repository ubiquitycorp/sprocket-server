package com.ubiquity.sprocket.analytics.recommendation;

import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
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
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Gender;
import com.ubiquity.sprocket.domain.GroupMembership;
import com.ubiquity.sprocket.repository.GroupMembershipRepository;
import com.ubiquity.sprocket.repository.GroupMembershipRepositoryJpaImpl;

public class RecommendationEngineTest implements UserMembershipListener {

	private RecommendationEngine engine;
	private GroupMembershipRepository membershipRepository;
	private UserRepository userRepository;
	
	private Logger log = LoggerFactory.getLogger(getClass());
		
	@Before
	public void setUp() throws Exception {
		Configuration config = new PropertiesConfiguration("test.properties");
		engine = new RecommendationEngineSparkImpl(config, this);		
		// add gender as a dimension
		engine.addDimension(Dimension.createFromEnum("gender", Gender.class));
		
		membershipRepository = new GroupMembershipRepositoryJpaImpl();
		userRepository = new UserRepositoryJpaImpl();
	}

	
	@Test
	public void testKMeansFindsNearestKClosestPoints() {
		log.debug("adding instances to the instnace space for Facebook contacts...");
		User jack = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		Contact.Builder contactBuilder = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(jack, ExternalNetwork.Facebook);
		contactBuilder.gender(Gender.Male);
		persistUser(jack);
		engine.updateProfileRecord(contactBuilder.build());
		
		User john = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		contactBuilder = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(john, ExternalNetwork.Facebook);
		contactBuilder.gender(Gender.Male);
		persistUser(john);
		engine.updateProfileRecord(contactBuilder.build());
		
		User joe = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		contactBuilder = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(joe, ExternalNetwork.Facebook);
		contactBuilder.gender(Gender.Male);
		persistUser(joe);
		engine.updateProfileRecord(contactBuilder.build());
		
		User jill = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		contactBuilder = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(jill, ExternalNetwork.Facebook);
		contactBuilder.gender(Gender.Female);
		persistUser(jill);
		engine.updateProfileRecord(contactBuilder.build());
		
		User jane = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		contactBuilder = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(jane, ExternalNetwork.Facebook);
		contactBuilder.gender(Gender.Female);
		persistUser(jane);
		engine.updateProfileRecord(contactBuilder.build());
		
		// running train will train the model (create the clusters and centroids) and also classify the existing Contacts used in the
		// computation
		engine.train();
		
		// find membership for john, jack, and joe
		List<GroupMembership> membership = membershipRepository.findAllByUserId(john.getUserId());
		GroupMembership johnMembership = membership.get(0);
		membership = membershipRepository.findAllByUserId(jack.getUserId());
		GroupMembership jackMembership = membership.get(0);
		membership = membershipRepository.findAllByUserId(joe.getUserId());
		GroupMembership joeMembership = membership.get(0);
		// now test that the group classifications worked properly by gender
		Assert.assertEquals(johnMembership.getGroupIdentifier(), jackMembership.getGroupIdentifier());
		Assert.assertEquals(joeMembership.getGroupIdentifier(), jackMembership.getGroupIdentifier());
		
		// find and very the girls are in their own group
		membership = membershipRepository.findAllByUserId(jill.getUserId());
		GroupMembership jillMembership = membership.get(0);
		membership = membershipRepository.findAllByUserId(jane.getUserId());
		GroupMembership janeMembership = membership.get(0);
		Assert.assertEquals(jillMembership.getGroupIdentifier(), janeMembership.getGroupIdentifier());
		
		// now classify a new user / contact without after training the model
		User jenny = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		contactBuilder = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(jenny, ExternalNetwork.Facebook);
		contactBuilder.gender(Gender.Female);
		persistUser(jenny);
		
		engine.updateProfileRecord(contactBuilder.build());
		
		engine.classify();
		
		// verify that the model is working...jenny should clustered with the girls
		membership = membershipRepository.findAllByUserId(jenny.getUserId());
		GroupMembership jennyMembership = membership.get(0);
		Assert.assertEquals(jennyMembership.getGroupIdentifier(), jillMembership.getGroupIdentifier());


	}


	private void persistUser(User user) {
		EntityManagerSupport.beginTransaction();
		userRepository.create(user);
		EntityManagerSupport.commit();
	}


	@Override
	public void didAssignMembershipForExternalNetwork(User user,
			ExternalNetwork network, String group) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void didAssignGlobalMembership(User user, String group) {
		log.info("user {}", user);
		
		EntityManagerSupport.beginTransaction();
		membershipRepository.create(new GroupMembership(null, user, group));
		EntityManagerSupport.commit();

	}

}
