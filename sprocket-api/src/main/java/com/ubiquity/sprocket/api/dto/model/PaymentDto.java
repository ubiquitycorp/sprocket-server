package com.ubiquity.sprocket.api.dto.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class PaymentDto {

	@NotNull
	@Size(min = 10, max = 100)
	private String accountNumber;

	@NotNull
	private Integer securityCode;

	@NotNull
	@Size(min = 1, max = 2)
	private Integer expirationMonth;

	@NotNull
	@Size(min = 4, max = 4)
	private Integer expirationYear;

	@NotNull
	@Size(min = 2, max = 100)
	private String name;

	private Long paymentMethodId;

	private String accountNumberMask;

	public Long getPaymentMethodId() {
		return paymentMethodId;
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

	public String getAccountNumberMask() {
		return accountNumberMask;
	}

	public static class Builder {
		private String accountNumber;
		private Integer securityCode;
		private Integer expirationMonth;
		private Integer expirationYear;
		private String name;
		private Long paymentMethodId;
		private String accountNumberMask;

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

		public Builder paymentMethodId(Long paymentMethodId) {
			this.paymentMethodId = paymentMethodId;
			return this;
		}

		public Builder accountNumberMask(String accountNumberMask) {
			this.accountNumberMask = accountNumberMask;
			return this;
		}

		public PaymentDto build() {
			return new PaymentDto(this);
		}
	}

	private PaymentDto(Builder builder) {
		this.accountNumber = builder.accountNumber;
		this.securityCode = builder.securityCode;
		this.expirationMonth = builder.expirationMonth;
		this.expirationYear = builder.expirationYear;
		this.name = builder.name;
		this.paymentMethodId = builder.paymentMethodId;
		this.accountNumberMask = builder.accountNumberMask;
	}
}
