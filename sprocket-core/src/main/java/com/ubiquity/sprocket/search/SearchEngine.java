package com.ubiquity.sprocket.search;

import java.util.List;

import com.ubiquity.sprocket.domain.Document;

public interface SearchEngine {
	
	public void addDocument(Document document);

	public List<Document> searchDocuments(String searchTerm);

}
