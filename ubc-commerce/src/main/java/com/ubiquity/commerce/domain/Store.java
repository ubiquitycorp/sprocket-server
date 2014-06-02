package com.ubiquity.commerce.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/***
 * Interface exposing CRUD methods for the Category entity
 * @author peter.tadros
 *
 */
@Entity
@Table(name = "store")
public class Store {

	@Id
	@GeneratedValue
	@Column(name = "store_id")
	private Long storeId;

	@Column(name = "store_name", nullable = false)
	private String name;
	
	@Column(name = "logo", nullable = true)
	private String logo;
	
	@Column(name = "solution_id", nullable = false)
	private Long solutionId;
	
	@Column(name = "security_token", nullable = false)
	private String securityToken;
	
	@Column(name = "bun_id", nullable = false)
	private Long bunId;
	
	@Column(name = "terms", nullable = true)
	private String terms;
	
	@Column(name = "how_to_redeem", nullable = true)
	private String howToRedeem;
	
	@Column(name = "is_active", nullable = false)
	private Boolean isActive;

	/***
	 * Default constructor required by JPA
	 */
	public Store() {
	}
	
	public Long getStoreId() {
		return storeId;
	}

	public String getName() {
		return name;
	}

	public String getLogo() {
		return logo;
	}
	
	public void setLogo(String logo) {
		this.logo = logo;
	}

	public Long getSolutionId() {
		return solutionId;
	}

	public String getSecurityToken() {
		return securityToken;
	}

	public Long getBunId() {
		return bunId;
	}

	public String getTerms() {
		return terms;
	}

	public String getHowToRedeem() {
		return howToRedeem;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public static class Builder {
		private Long storeId;
		private String name;
		private String logo;
		private Long solutionId;
		private String securityToken;
		private Long bunId;
		private String terms;
		private String howToRedeem;
		private Boolean isActive;

		public Builder storeId(Long storeId) {
			this.storeId = storeId;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}
		
		public Builder logo(String logo) {
			this.logo = logo;
			return this;
		}
		
		public Builder solutionId(Long solutionId) {
			this.solutionId = solutionId;
			return this;
		}
		
		public Builder securityToken(String securityToken) {
			this.securityToken = securityToken;
			return this;
		}
		
		public Builder bunId(Long bunId) {
			this.bunId = bunId;
			return this;
		}
		
		public Builder terms(String terms) {
			this.terms = terms;
			return this;
		}
		
		public Builder howToRedeem(String howToRedeem) {
			this.howToRedeem = howToRedeem;
			return this;
		}
		
		public Builder isActive(Boolean isActive) {
			this.isActive = isActive;
			return this;
		}
		
		public Store build() {
			return new Store(this);
		}
	}
	
	private Store(Builder builder) {
		this.storeId = builder.storeId;
		this.name = builder.name;
		this.logo = builder.logo;
		this.solutionId = builder.solutionId;
		this.securityToken = builder.securityToken;
		this.bunId = builder.bunId;
		this.terms = builder.terms;
		this.howToRedeem = builder.howToRedeem;
		this.isActive = builder.isActive;
	}
}
