package com.ubiquity.giftsender.api.dto.model;

public class RedeemResult {
	
	private Long orderId;
	
	private String  pinId;
	
	private String partnerCertId;
	
	private String barcodeImageUrl;

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getPinId() {
		return pinId;
	}

	public void setPinId(String pinId) {
		this.pinId = pinId;
	}

	public String getPartnerCertId() {
		return partnerCertId;
	}

	public void setPartnerCertId(String partnerCertId) {
		this.partnerCertId = partnerCertId;
	}

	public String getBarcodeImageUrl() {
		return barcodeImageUrl;
	}

	public void setBarcodeImageUrl(String barcodeImageUrl) {
		this.barcodeImageUrl = barcodeImageUrl;
	}
}
