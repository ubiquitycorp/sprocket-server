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

/***
 * Entity 
 * @author chris
 *
 */
@Entity
@Table(name = "external_interest", indexes = {
		@Index(name="idx_external_network_name", columnList = "external_network, name", unique = true)
		})
public class ExternalInterest {
	
	@Id
	@GeneratedValue
	@Column(name = "external_interest_id")
	private Long externalInterestId;
	
	@Column(name = "name")
	private String name;
	
	@ManyToOne
	@JoinColumn(name = "interest_id")
	private Interest interest;
	
	@Column(name="external_network")
	private ExternalNetwork externalNetwork;

	/**
	 * Default constructor required by JPA
	 * 
	 */
	protected ExternalInterest() {}
	
	/***
	 * Creates an interest mapping with required properties
	 * 
	 * @param name
	 * @param interest
	 * @param externalNetwork
	 */
	public ExternalInterest(String name, Interest interest,
			ExternalNetwork externalNetwork) {
		super();
		this.name = name;
		this.interest = interest;
		this.externalNetwork = externalNetwork;
	}

	public String getName() {
		return name;
	}

	public Interest getInterest() {
		return interest;
	}

	public ExternalNetwork getExternalNetwork() {
		return externalNetwork;
	}

	public Long getExternalInterestId() {
		return externalInterestId;
	}
	
	
	
	

}
