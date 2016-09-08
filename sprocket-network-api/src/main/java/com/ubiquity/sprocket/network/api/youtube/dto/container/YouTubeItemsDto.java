package com.ubiquity.sprocket.network.api.youtube.dto.container;

import java.util.LinkedList;
import java.util.List;

public class YouTubeItemsDto {
	
	private String nextPageToken;
	private String prevPageToken;
	private String etag;
	private List<Object> items = new LinkedList<Object>();

	public List<Object> getItems() {
		return items;
	}

	public String getNextPageToken() {
		return nextPageToken;
	}

	public String getPrevPageToken() {
		return prevPageToken;
	}

	public String getEtag() {
		return etag;
	}

	public void setEtag(String etag) {
		this.etag = etag;
		
	}
	
	public void setPaging(String etag, String nextPageToken, String prevPageToken) {
		this.etag = etag;
		this.nextPageToken = nextPageToken;
		this.prevPageToken = prevPageToken;
		
	}
	

}
