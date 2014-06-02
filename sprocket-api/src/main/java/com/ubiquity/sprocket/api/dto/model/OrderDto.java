package com.ubiquity.sprocket.api.dto.model;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

/***
 * Shared dto object encapsulating an item in the context of an order
 * 
 * @author chris
 *
 */
public class OrderDto {

	@NotNull
	private Long itemId;
	
	@NotNull
	private Long contactId;
	
	@NotNull
	private Double denomination;
	
	@NotNull
	private String message;
	
	private Long orderId;
	private ContactDto recipient;
	private ItemDto item;
	private Map<String, Object> selectedOptions = new HashMap<String, Object>();
	private Double total;
	private Long created;
	private PaymentDto payment;
	private Boolean isRedeemed;
	private String  pinId;
	private String partnerCertId;
	private String barcodeImageUrl;

	public ItemDto getItem() {
		return item;
	}

	public PaymentDto getPayment() {
		return payment;
	}

	public Map<String, Object> getSelectedOptions() {
		return selectedOptions;
	}

	public Double getTotal() {
		return total;
	}

	public Long getOrderId() {
		return orderId;
	}

	public ContactDto getRecipient() {
		return recipient;
	}

	public Long getCreated() {
		return created;
	}

	public Boolean getIsRedeemed() {
		return isRedeemed;
	}
	
	public String getPinId() {
		return pinId;
	}

	public String getPartnerCertId() {
		return partnerCertId;
	}

	public String getBarcodeImageUrl() {
		return barcodeImageUrl;
	}
	
	


	public Long getItemId() {
		return itemId;
	}

	public Long getContactId() {
		return contactId;
	}

	public Double getDenomination() {
		return denomination;
	}

	public String getMessage() {
		return message;
	}




	public static class Builder {
		private Long orderId;
		private ContactDto recipient;
		private ItemDto item;
		private Map<String, Object> selectedOptions;
		private Double total;
		private Long created;
		private PaymentDto payment;
		private Boolean isRedeemed;
		private String  pinId;
		private String partnerCertId;
		private String barcodeImageUrl;
		private String message;
		
		public Builder orderId(Long orderId) {
			this.orderId = orderId;
			return this;
		}

		public Builder recipient(ContactDto recipient) {
			this.recipient = recipient;
			return this;
		}

		public Builder item(ItemDto item) {
			this.item = item;
			return this;
		}

		public Builder selectedOptions(Map<String, Object> selectedOptions) {
			this.selectedOptions = selectedOptions;
			return this;
		}

		public Builder total(Double total) {
			this.total = total;
			return this;
		}

		public Builder created(Long created) {
			this.created = created;
			return this;
		}

		public Builder payment(PaymentDto payment) {
			this.payment = payment;
			return this;
		}
		
		public Builder isRedeemed(Boolean isRedeemed) {
			this.isRedeemed = isRedeemed;
			return this;
		}
		
		public Builder partnerCertId(String partnerCertId) {
			this.partnerCertId = partnerCertId;
			return this;
		}
		
		public Builder pinId(String pinId) {
			this.pinId = pinId;
			return this;
		}
		
		public Builder barcodeImageUrl(String barcodeImageUrl) {
			this.barcodeImageUrl = barcodeImageUrl;
			return this;
		}
		
		public Builder message(String message){
			this.message = message;
			return this;
		}

		public OrderDto build() {
			return new OrderDto(this);
		}
	}

	private OrderDto(Builder builder) {
		this.orderId = builder.orderId;
		this.recipient = builder.recipient;
		this.item = builder.item;
		this.selectedOptions = builder.selectedOptions;
		this.total = builder.total;
		this.created = builder.created;
		this.payment = builder.payment;
		this.isRedeemed = builder.isRedeemed;
		this.barcodeImageUrl = builder.barcodeImageUrl;
		this.partnerCertId = builder.partnerCertId;
		this.pinId = builder.pinId;
		this.message = builder.message;
	}
}
