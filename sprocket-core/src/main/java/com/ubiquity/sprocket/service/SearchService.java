package com.ubiquity.sprocket.service;

import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.sprocket.domain.Document;
import com.ubiquity.sprocket.search.SearchEngine;
import com.ubiquity.sprocket.search.SearchEngineSolrjImpl;

public class SearchService {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private SearchEngine searchEngine;
	
	public SearchService(Configuration config) {
		log.debug("Using solr api path: {}", config.getProperty("solr.api.path"));
		searchEngine = new SearchEngineSolrjImpl(config);
	}
	
	public void addDocument(Document document) {
		searchEngine.addDocument(document);
	}
	
	public List<Document> searchDocuments(String searchTerm, Long userId) {
		return searchEngine.searchDocuments(searchTerm, userId);
	}
}
