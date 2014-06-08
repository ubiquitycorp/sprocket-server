package com.ubiquity.sprocket.api.dto.containers;

import java.util.HashMap;
import java.util.Map;

public class MostPopularDto {
	
	private Map<String, Long> searchTerms = new HashMap<String, Long>();
	private Map<String, Long> socialNetworks = new HashMap<String, Long>();
	public Map<String, Long> getSearchTerms() {
		return searchTerms;
	}
	public Map<String, Long> getSocialNetworks() {
		return socialNetworks;
	}
	
}
