package com.ubiquity.commerce.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ubiquity.identity.domain.Identity;
import com.ubiquity.identity.domain.User;

@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue
	@Column(name = "order_id")
	private Long orderId;

	@Column(name = "message", nullable = true, length = 300)
	private String message;

	@Column(name = "created_at", nullable = false)
	private Long createdAt;

	@Column(name = "price", nullable = false)
	private Double denomination;

	@ManyToOne
	@JoinColumn(name = "item_id", nullable = false)
	private Item item;

	@ManyToOne
	@JoinColumn(name = "purchased_for_identity_id", nullable = false)
	private Identity purchasedFor;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "payment_method_id", nullable = false)
	private PaymentMethod paymentMethod;

	@Column(name = "is_redeemed", nullable = false)
	private Boolean isRedeemed;

	@Column(name = "giftango_transaction_id", nullable = true)
	private Long giftangoTransactionId;

	@Column(name = "giftango_pin_id", nullable = true)
	private String giftangoPinId;

	@Column(name = "partner_cert_id", nullable = true)
	private String partnerCertId;

	@Column(name = "giftango_cert_id", nullable = true)
	private String giftangoCertId;

	@Column(name = "cert_barcode_url", nullable = true)
	private String certBarcodeUrl;

	@Column(name = "how_to_redeem", nullable = true, length = 1000)
	private String howToRedeem;

	@Column(name = "terms", nullable = true, length = 1000)
	private String terms;

	/***
	 * Default constructor required by JPA
	 */
	public Order() {
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}

	public Double getDenomination() {
		return denomination;
	}

	public void setDenomination(Double denomination) {
		this.denomination = denomination;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Identity getPurchasedFor() {
		return purchasedFor;
	}

	public User getUser() {
		return user;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public Boolean getIsRedeemed() {
		return isRedeemed;
	}

	public void setIsRedeemed(Boolean isRedeemed) {
		this.isRedeemed = isRedeemed;
	}

	public Long getGiftangoTransactionId() {
		return giftangoTransactionId;
	}

	public void setGiftangoTransactionId(Long giftangoTransactionId) {
		this.giftangoTransactionId = giftangoTransactionId;
	}

	public String getGiftangoPinId() {
		return giftangoPinId;
	}

	public void setGiftangoPinId(String giftangoPinId) {
		this.giftangoPinId = giftangoPinId;
	}

	public String getPartnerCertId() {
		return partnerCertId;
	}

	public void setPartnerCertId(String partnerCertId) {
		this.partnerCertId = partnerCertId;
	}

	public String getGiftangoCertId() {
		return giftangoCertId;
	}

	public void setGiftangoCertId(String giftangoCertId) {
		this.giftangoCertId = giftangoCertId;
	}

	public String getCertBarcodeUrl() {
		return certBarcodeUrl;
	}

	public void setCertBarcodeUrl(String certBarcodeUrl) {
		this.certBarcodeUrl = certBarcodeUrl;
	}

	public String getHowToRedeem() {
		return howToRedeem;
	}

	public void setHowToRedeem(String howToRedeem) {
		this.howToRedeem = howToRedeem;
	}

	public String getTerms() {
		return terms;
	}

	public void setTerms(String terms) {
		this.terms = terms;
	}

	public static class Builder {
		private Long orderId;
		private String message;
		private Long createdAt;
		private Double denomination;
		private Item item;
		private Identity purchasedFor;
		private User user;
		private PaymentMethod paymentMethod;
		private Boolean isRedeemed;
		private Long giftangoTransactionId;
		private String giftangoPinId;
		private String partnerCertId;
		private String giftangoCertId;
		private String certBarcodeUrl;
		private String howToRedeem;
		private String terms;

		public Builder orderId(Long orderId) {
			this.orderId = orderId;
			return this;
		}

		public Builder message(String message) {
			this.message = message;
			return this;
		}

		public Builder createdAt(Long createdAt) {
			this.createdAt = createdAt;
			return this;
		}

		public Builder denomination(Double denomination) {
			this.denomination = denomination;
			return this;
		}

		public Builder item(Item item) {
			this.item = item;
			return this;
		}

		public Builder purchasedFor(Identity purchasedFor) {
			this.purchasedFor = purchasedFor;
			return this;
		}

		public Builder user(User user) {
			this.user = user;
			return this;
		}

		public Builder paymentMethod(PaymentMethod paymentMethod) {
			this.paymentMethod = paymentMethod;
			return this;
		}

		public Builder isRedeemed(Boolean isRedeemed) {
			this.isRedeemed = isRedeemed;
			return this;
		}

		public Builder giftangoTransactionId(Long giftangoTransactionId) {
			this.giftangoTransactionId = giftangoTransactionId;
			return this;
		}

		public Builder giftangoPinId(String giftangoPinId) {
			this.giftangoPinId = giftangoPinId;
			return this;
		}

		public Builder partnerCertId(String partnerCertId) {
			this.partnerCertId = partnerCertId;
			return this;
		}

		public Builder giftangoCertId(String giftangoCertId) {
			this.giftangoCertId = giftangoCertId;
			return this;
		}

		public Builder certBarcodeUrl(String certBarcodeUrl) {
			this.certBarcodeUrl = certBarcodeUrl;
			return this;
		}

		public Builder howToRedeem(String howToRedeem) {
			this.howToRedeem = howToRedeem;
			return this;
		}

		public Builder terms(String terms) {
			this.terms = terms;
			return this;
		}

		public Order build() {
			return new Order(this);
		}
	}

	private Order(Builder builder) {
		this.orderId = builder.orderId;
		this.message = builder.message;
		this.createdAt = builder.createdAt;
		this.denomination = builder.denomination;
		this.item = builder.item;
		this.purchasedFor = builder.purchasedFor;
		this.user = builder.user;
		this.paymentMethod = builder.paymentMethod;
		this.isRedeemed = builder.isRedeemed;
		this.giftangoTransactionId = builder.giftangoTransactionId;
		this.giftangoPinId = builder.giftangoPinId;
		this.partnerCertId = builder.partnerCertId;
		this.giftangoCertId = builder.giftangoCertId;
		this.certBarcodeUrl = builder.certBarcodeUrl;
		this.howToRedeem = builder.howToRedeem;
		this.terms = builder.terms;
	}
}
