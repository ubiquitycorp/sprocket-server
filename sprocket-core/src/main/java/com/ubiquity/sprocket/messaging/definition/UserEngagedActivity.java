package com.ubiquity.sprocket.messaging.definition;

import com.ubiquity.integration.domain.Activity;

public class UserEngagedActivity {

	private Long userId;
	private Activity activity;

	public UserEngagedActivity(Long userId, Activity activity) {
		super();
		this.userId = userId;
		this.activity = activity;
	}
	
	

	@Override
	public String toString() {
		return "UserEngagedActivity [userId=" + userId + ", activity="
				+ activity + "]";
	}



	public Long getUserId() {
		return userId;
	}

	public Activity getActivity() {
		return activity;
	}
}
