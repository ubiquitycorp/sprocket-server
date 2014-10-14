package com.ubiquity.sprocket.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ubiquity.integration.domain.Activity;

@Entity
@Table(name = "recommended_activity", indexes = {
		@Index(name="idx_activity_group_identifier", columnList = "activity_id, group_identifier", unique = true)
		})
public class RecommendedActivity {
	
	@Id
	@GeneratedValue
	@Column(name = "recommended_activity_id")
	private Long recommendedActivityId;
	
	@ManyToOne
	@JoinColumn(name = "activity_id")
	private Activity activity;
	
	@Column(name = "group_identifier")
	private String groupIdentifier;

	/***
	 * Default constructor required by JPA
	 */
	protected RecommendedActivity() {}

	public RecommendedActivity(Activity activity, String groupIdentifier) {
		this.activity = activity;
		this.groupIdentifier = groupIdentifier;
	}

	protected Long getRecommendedActivityId() {
		return recommendedActivityId;
	}

	protected Activity getActivity() {
		return activity;
	}

	protected String getGroupIdentifier() {
		return groupIdentifier;
	}




}
