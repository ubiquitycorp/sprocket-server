package com.ubiquity.sprocket.service;

import java.util.List;
import java.util.UUID;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.ubiquity.social.domain.VideoContent;
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
		document.getFields().put("general_content", searchTerm);
		document.getFields().put("data_type", VideoContent.class.getSimpleName());
		document.getFields().put("user_id", 1l);
		document.getFields().put("id", UUID.randomUUID().toString());
		searchService.addDocument(document);
		
		List<Document> documents  = searchService.searchDocuments(searchTerm, 1l);
		Assert.assertTrue(documents.size() == 1);
		Document result = documents.get(0);
		Assert.assertEquals(searchTerm, result.getFields().get("general_content"));
		Assert.assertEquals(VideoContent.class.getSimpleName(), result.getFields().get("data_type"));
		Assert.assertEquals(1l, result.getFields().get("user_id"));
		
		// make sure other users don't see this
		documents  = searchService.searchDocuments(searchTerm, 2l);
		Assert.assertTrue(documents.size() == 0);
				

	}
	
	
	
	
	

}
