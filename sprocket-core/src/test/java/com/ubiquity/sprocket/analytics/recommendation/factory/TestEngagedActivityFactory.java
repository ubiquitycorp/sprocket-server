package com.ubiquity.sprocket.analytics.recommendation.factory;

import com.ubiquity.identity.domain.User;
import com.ubiquity.integration.domain.Activity;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.factory.TestActivityFactory;
import com.ubiquity.sprocket.domain.EngagedActivity;

public class TestEngagedActivityFactory {

	/***
	 * Returns enaged activity for persistence
	 * 
	 * @param u
	 * @param activity
	 * @return
	 */
	public static EngagedActivity createEngagedActivity(User user, ExternalNetwork network) {
		Activity activity = TestActivityFactory.createActivityWithMininumRequirements(user, network, "http://my.link.com");
		return new EngagedActivity(user, activity);
	}

}
