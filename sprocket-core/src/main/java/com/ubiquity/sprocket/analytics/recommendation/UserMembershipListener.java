package com.ubiquity.sprocket.analytics.recommendation;

import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.identity.domain.User;

public interface UserMembershipListener {
	
	void didAssignMembership(User user, ExternalNetwork network, String group);
}
