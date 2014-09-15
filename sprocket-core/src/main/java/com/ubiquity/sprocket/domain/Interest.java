package com.ubiquity.sprocket.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "interest")
public class Interest {
	
	@Id
	@GeneratedValue
	@Column(name = "interest_id")
	private Long interestId;
	
	@Column(name = "name", unique =  true)
	private String name;
	
	
	@OneToMany(mappedBy = "parent", cascade = { CascadeType.ALL }, orphanRemoval = true)
	private Set<Interest> children = new HashSet<Interest>();
	
	@ManyToOne
	@JoinColumn(name = "parent_id")
	private Interest parent;

	/**
	 * Required by JPA
	 */
	protected Interest() {}
	
	
	public Interest(String name) {
		this.name = name;
	}
	/***
	 * Create interest with optional parent
	 * 
	 * @param name
	 * @param parent
	 */
	public Interest(String name, Interest parent) {
		super();
		this.name = name;
		this.parent = parent;
	}

	public void setParent(Interest parent) {
		this.parent = parent;
	}

	public Long getInterestId() {
		return interestId;
	}

	public String getName() {
		return name;
	}

	public Set<Interest> getChildren() {
		return children;
	}

	public Interest getParent() {
		return parent;
	}
	
	public void addChild(Interest interest) {
		children.add(interest);
		interest.setParent(interest);
	}
	
	
	
	
	

}
