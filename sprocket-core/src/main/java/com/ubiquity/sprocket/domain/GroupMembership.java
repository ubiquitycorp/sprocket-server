package com.ubiquity.sprocket.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.identity.domain.User;

/***
 * Domain entity for tracking a user's membership for an external network; an external network may be null, which tracks membership
 * outside the context of a network
 * 
 * @author chris
 *
 */
@Entity
@Table(name = "group_membership")
public class GroupMembership {
	
	@Id
	@GeneratedValue
	@Column(name = "group_membership_id")
	private Long groupMembershipId;
	
	@Column(name = "external_network", nullable = true)
	private ExternalNetwork externalNetwork;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	
	@Column(name = "group_identifier", nullable = false)
	private String groupIdentifier;
	
	@Column(name = "group_assignment_filter", nullable = false)
	private GroupAssignmentFilter assignmentFilter;
	
	
	/**
	 * Required by JPA
	 */
	protected GroupMembership() {}
	
	/***
	 * Creates a membership object for an external profile
	 * 
	 * @param externalNetwork
	 * @param user
	 * @param groupIdentifier
	 */
	public GroupMembership(ExternalNetwork externalNetwork, User user,
			String groupIdentifier) {
		this.externalNetwork = externalNetwork;
		this.user = user;
		this.groupIdentifier = groupIdentifier;
		this.assignmentFilter = GroupAssignmentFilter.ExternalProfile;
	}
	
	public Long getGroupMembershipId() {
		return groupMembershipId;
	}
	public ExternalNetwork getExternalNetwork() {
		return externalNetwork;
	}
	public User getUser() {
		return user;
	}
	public String getGroupIdentifier() {
		return groupIdentifier;
	}

	

}
