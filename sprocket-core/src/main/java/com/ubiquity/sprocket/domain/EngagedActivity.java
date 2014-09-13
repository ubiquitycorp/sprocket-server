package com.ubiquity.sprocket.domain;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.ubiquity.identity.domain.User;
import com.ubiquity.social.domain.Activity;

@Entity
@DiscriminatorValue("activity")
public class EngagedActivity extends EngagedItem {

	@ManyToOne(cascade = { CascadeType.PERSIST })
	@JoinColumn(name = "activity_id")
	private Activity activity;
	
	protected EngagedActivity() {}
	
	public EngagedActivity(User user, Activity activity) {
		super(user);
		this.activity = activity;
	}

	public Activity getActivity() {
		return activity;
	}
	
	
}
