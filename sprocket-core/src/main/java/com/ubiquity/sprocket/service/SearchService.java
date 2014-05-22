package com.ubiquity.sprocket.service;

import java.util.List;

import org.apache.commons.configuration.Configuration;

import com.ubiquity.sprocket.domain.Document;
import com.ubiquity.sprocket.search.SearchEngine;
import com.ubiquity.sprocket.search.SearchEngineSolrjImpl;

public class SearchService {
	
	private SearchEngine searchEngine;
	
	public SearchService(Configuration config) {
		searchEngine = new SearchEngineSolrjImpl(config);
	}
	
	public void addDocument(Document document) {
		searchEngine.addDocument(document);
	}
	
	public List<Document> searchDocuments(String searchTerm) {
		return searchEngine.searchDocuments(searchTerm);
	}
}
