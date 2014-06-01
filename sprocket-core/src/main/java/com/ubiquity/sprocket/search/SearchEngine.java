package com.ubiquity.sprocket.search;

import java.util.List;
import java.util.Map;

import com.ubiquity.sprocket.domain.Document;

public interface SearchEngine {
	
	/***
	 * Adds a document to the search index
	 * 
	 * @param document
	 */
	public void addDocument(Document document);
	
	/***
	 * Adds a list of documents to the search index
	 * 
	 * @param documents
	 */
	public void addDocuments(List<Document> documents);

	/***
	 * 
	 * Search for documents matching the value for this map of field / value pairs
     *
	 * @param searchTerm
	 * @param fields
	 * @return
	 */
	public List<Document> searchDocuments(String searchTerm, String[] fields, Map<String, Object> filter);

	/***
	 * Removes all documents 
	 */
	public void deleteAllDocuments();

}
