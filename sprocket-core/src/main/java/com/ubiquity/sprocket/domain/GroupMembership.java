package com.ubiquity.sprocket.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.User;

/***
 * Domain entity for tracking a user's membership for an external network; an external network may be null, which tracks membership
 * outside the context of a network
 * 
 * @author chris
 *
 */
@Entity
@Table(name = "group_membership", indexes = {
		@Index(name="idx_external_network_group_identifier_identity", columnList = "external_network, group_identifier, external_identity_id", unique = true),
		@Index(name="idx_external_network_group_identifier_user", columnList = "external_network, group_identifier, user_id", unique = true)
		})
public class GroupMembership {
	
	@Id
	@GeneratedValue
	@Column(name = "group_membership_id")
	private Long groupMembershipId;
	
	@Column(name = "external_network", nullable = true)
	private ExternalNetwork externalNetwork;
	
	@ManyToOne
	@JoinColumn(name = "external_identity_id", nullable = true)
	private ExternalIdentity externalIdentity;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = true)
	private User user;
	
	
	@Column(name = "group_identifier", nullable = false)
	private String groupIdentifier;
	
	
	/**
	 * Required by JPA
	 */
	protected GroupMembership() {}
	
	
	/***
	 * Creates a membership record for the global profile
	 * 
	 * @param user
	 * @param groupIdentifier
	 */
	public GroupMembership(User user, String groupIdentifier) {
		this.user = user;
		this.groupIdentifier = groupIdentifier;
	}
	/***
	 * Creates a membership object for an external profile
	 * 
	 * @param externalNetwork
	 * @param user
	 * @param groupIdentifier
	 */
	public GroupMembership(ExternalIdentity identity, User user,
			String groupIdentifier) {
		this.externalNetwork = ExternalNetwork.getNetworkById(identity.getExternalNetwork());
		this.externalIdentity = identity;
		this.user = user;
		this.groupIdentifier = groupIdentifier;
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
