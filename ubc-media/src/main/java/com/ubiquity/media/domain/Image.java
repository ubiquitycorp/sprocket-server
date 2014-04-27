package com.ubiquity.media.domain;

import java.io.InputStream;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.niobium.repository.cloud.RemoteAsset;


@Embeddable
public class Image implements RemoteAsset {

	@Transient
	private InputStream inputStream;
	
	@Transient
	private String itemKey;
	
	@Transient
	private Long contentLength;
	
	@Transient
	private ImageFormat format;
	
	private String url;
	
	/**
	 * Default constructor required by JPA
	 */
	protected Image() {}
	
	
	
	/****
	 * 
	 * Creates a new image with properties required to fulfill the RemoteAsset contract
	 * and an image format
	 * 
	 * @param itemKey
	 * @param inputStream
	 * @param contentLength
	 * @param format
	 */
	public Image(String itemKey, InputStream inputStream, Long contentLength, ImageFormat format) {
		this.itemKey = itemKey;
		this.inputStream = inputStream;
		this.contentLength = contentLength;
		this.format = format;
	}
	
	
	/**
	 * Creates a new image with a url
	 * 
	 * @param url
	 */
	public Image(String url) {
		this.url = url;
	}
	
	public Long getContentLength() {
		return contentLength;
	}

	public String getItemKey() {
		return itemKey;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public ImageFormat getFormat() {
		return format;
	}

	public void setFormat(ImageFormat format) {
		this.format = format;
	}
	
	

}
