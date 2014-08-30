package com.ubiquity.sprocket.analytics.recommendation;

import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.identity.domain.User;

public interface UserMembershipListener {
	
	void didAssignMembershipForExternalNetwork(User user, ExternalNetwork network, String group);

	void didAssignGlobalMembership(User user, String group);
}
