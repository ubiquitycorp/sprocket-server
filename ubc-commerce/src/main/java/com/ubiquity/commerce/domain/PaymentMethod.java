package com.ubiquity.commerce.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.ubiquity.identity.domain.Identity;
import com.ubiquity.identity.domain.User;

/***
 * 
 * Class representing payment method identity
 * 
 * @author chris
 *
 */
@Entity
@Table(name = "payment_method")
public class PaymentMethod extends Identity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "created_date")
	private Long createdDate;

	@Column(name = "payment_method_token")
	private String paymentMethodToken;

	@Column(name = "account_number_mask")
	private String accountNumberMask;

	@Column(name = "payment_method_type")
	private PaymentMethodType paymentMethodType;

	@Column(name = "is_default", nullable = false)
	private Boolean isDefault;

	@Transient
	private String accountNumber;

	@Transient
	private Integer securityCode;

	@Transient
	private Integer expirationMonth;

	@Transient
	private Integer expirationYear;

	@Transient
	private String name;

	/***
	 * Default constructor required by JPA
	 */
	protected PaymentMethod() {
	}

	public void setPaymentMethodToken(String paymentMethodToken) {
		this.paymentMethodToken = paymentMethodToken;
	}

	public void setAccountNumberMask(String accountNumberMask) {
		this.accountNumberMask = accountNumberMask;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Long getDateCreated() {
		return createdDate;
	}

	public String getPaymentMethodToken() {
		return paymentMethodToken;
	}

	public String getAccountNumberMask() {
		return accountNumberMask;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public Integer getSecurityCode() {
		return securityCode;
	}

	public Integer getExpirationMonth() {
		return expirationMonth;
	}

	public Integer getExpirationYear() {
		return expirationYear;
	}

	public String getName() {
		return name;
	}

	public PaymentMethodType getPaymentMethodType() {
		return paymentMethodType;
	}

	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	public static class Builder {
		private Long createdDate;
		private String paymentMethodToken;
		private String accountNumberMask;
		private PaymentMethodType paymentMethodType;
		private Boolean isDefault;
		private String accountNumber;
		private Integer securityCode;
		private Integer expirationMonth;
		private Integer expirationYear;
		private String name;
		private User user;

		public Builder createdDate(Long createdDate) {
			this.createdDate = createdDate;
			return this;
		}

		public Builder paymentMethodToken(String paymentMethodToken) {
			this.paymentMethodToken = paymentMethodToken;
			return this;
		}

		public Builder accountNumberMask(String accountNumberMask) {
			this.accountNumberMask = accountNumberMask;
			return this;
		}

		public Builder paymentMethodType(PaymentMethodType paymentMethodType) {
			this.paymentMethodType = paymentMethodType;
			return this;
		}

		public Builder isDefault(Boolean isDefault) {
			this.isDefault = isDefault;
			return this;
		}

		public Builder accountNumber(String accountNumber) {
			this.accountNumber = accountNumber;
			return this;
		}

		public Builder securityCode(Integer securityCode) {
			this.securityCode = securityCode;
			return this;
		}

		public Builder expirationMonth(Integer expirationMonth) {
			this.expirationMonth = expirationMonth;
			return this;
		}

		public Builder expirationYear(Integer expirationYear) {
			this.expirationYear = expirationYear;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}
		
		public Builder user(User user) {
			this.user = user;
			return this;
		}

		public PaymentMethod build() {
			return new PaymentMethod(this);
		}
	}

	private PaymentMethod(Builder builder) {
		this.createdDate = builder.createdDate;
		this.paymentMethodToken = builder.paymentMethodToken;
		this.accountNumberMask = builder.accountNumberMask;
		this.paymentMethodType = builder.paymentMethodType;
		this.isDefault = builder.isDefault;
		this.accountNumber = builder.accountNumber;
		this.securityCode = builder.securityCode;
		this.expirationMonth = builder.expirationMonth;
		this.expirationYear = builder.expirationYear;
		this.name = builder.name;
		super.user = builder.user;
	}
}
