package com.ubiquity.media.domain;

import javax.persistence.Transient;

import com.niobium.repository.cloud.RemoteAsset;

public class Image extends Media implements RemoteAsset {
	
	@Transient
	private ImageFormat format;
	
	/**
	 * Default constructor required by JPA
	 */
	protected Image() {}

	public ImageFormat getFormat() {
		return format;
	}
	
	public Image(String url) {
		super(url);
	}
	
	

	
	
	

}
