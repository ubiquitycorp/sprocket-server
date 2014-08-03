package com.ubiquity.sprocket.analytics.recommendation;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.Range;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.factory.TestUserFactory;
import com.ubiquity.integration.factory.TestContactFactory;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Gender;

public class RecommendationEngineTest {

	private RecommendationEngine engine;
	
	private Logger log = LoggerFactory.getLogger(getClass());
		
	@Before
	public void setUp() throws Exception {
		Configuration config = new PropertiesConfiguration("test.properties");
		engine = new RecommendationEngineSparkImpl(config);		
		// add gender as a dimension
		engine.addDimension(createFromEnum("gender", Gender.class));
	}
	
	private static Dimension createFromEnum(String name, Class<? extends Enum<?>> enumClass) {
		Range<Double> range = Range.between(0.0, new Double(enumClass.getEnumConstants().length));
		return new Dimension(name, range);
	}
	
	@Test
	public void testKMeansFindsNearestKClosestPoints() {
		log.debug("adding instances to the instnace space for Facebook contacts...");
		User jack = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		Contact.Builder contactBuilder = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(jack, ExternalNetwork.Facebook);
		contactBuilder.gender(Gender.Male);
		engine.updateProfileRecord(contactBuilder.build());
		
		User john = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		contactBuilder = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(john, ExternalNetwork.Facebook);
		contactBuilder.gender(Gender.Male);
		engine.updateProfileRecord(contactBuilder.build());
		
		User joe = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		contactBuilder = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(joe, ExternalNetwork.Facebook);
		contactBuilder.gender(Gender.Male);
		engine.updateProfileRecord(contactBuilder.build());
		
		User jill = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		contactBuilder = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(jill, ExternalNetwork.Facebook);
		contactBuilder.gender(Gender.Female);
		engine.updateProfileRecord(contactBuilder.build());
		
		User jane = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		contactBuilder = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(jane, ExternalNetwork.Facebook);
		contactBuilder.gender(Gender.Female);
		engine.updateProfileRecord(contactBuilder.build());
		
		// running train will train the model (create the clusters and centroids) and also classify the existing Contacts used in the
		// computation
		engine.train();
		
		String johnGroup = john.getGroups().get(0);
		String jackGroup = jack.getGroups().get(0);
		String joeGroup = jack.getGroups().get(0);
		// now test that the group classifications worked properly by gender
		Assert.assertEquals(johnGroup, jackGroup);
		Assert.assertEquals(joeGroup, jackGroup);
		
		String jillGroup = jill.getGroups().get(0);
		String janeGroup = jane.getGroups().get(0);
		Assert.assertEquals(jillGroup, janeGroup);
		
		// now classify a new user / contact without after training the model
		User jenny = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		contactBuilder = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(jenny, ExternalNetwork.Facebook);
		contactBuilder.gender(Gender.Female);
		engine.updateProfileRecord(contactBuilder.build());
		
		engine.classify();
		
		String jennyGroup = jenny.getGroups().get(0);
		Assert.assertEquals(jennyGroup, janeGroup);


	}

}
