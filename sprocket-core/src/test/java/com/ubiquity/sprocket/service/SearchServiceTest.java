package com.ubiquity.sprocket.service;

import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.ubiquity.sprocket.domain.Document;


public class SearchServiceTest {

	private SearchService searchService;
	private String searchTerm;
	
	@Before
	public void setUp() throws Exception {
		Configuration config = new PropertiesConfiguration("test.properties");
		searchService = new SearchService(config);
		searchTerm = UUID.randomUUID().toString();
	}
	
	@Test
	@Ignore
	public void testAddDocumentReturnsInBasicSearch() {
		Document document = new Document();
		document.getFields().put("name", searchTerm);
		document.getFields().put("id", UUID.randomUUID().toString());
		searchService.addDocument(document);
		
		List<Document> documents  = searchService.searchDocuments(searchTerm);
		Assert.assertTrue(documents.size() == 1);
		Document result = documents.get(0);
		Assert.assertEquals(searchTerm, result.getFields().get("name"));
	}
	
	

}
