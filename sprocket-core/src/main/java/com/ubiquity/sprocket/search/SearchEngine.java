package com.ubiquity.sprocket.search;

import java.util.List;
import java.util.Map;

import com.ubiquity.sprocket.domain.Document;
import com.ubiquity.sprocket.search.solr.SolrOperator;

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
	 * Search for indexed documents matching the value for this map of field / value pairs
     *
	 * @param searchTerm
	 * @param fields
	 * @return
	 */
	public List<Document> searchDocuments(String searchTerm, String[] fields, Map<String, Object> filter, SolrOperator operator);
	
	/***
	 * 
	 * Search for indexed documents matching the value for this map of field / value pairs
	 * and group by group field and adds limit to each group
     *
	 * @param searchTerm
	 * @param fields
	 * @return
	 */
	public List<Document> searchDocuments(String searchTerm, String[] fields, Map<String, Object> filters, 
			SolrOperator operator, String groupField, Integer groupLimit, Integer resultsLimit);
	
	/***
	 * Removes all documents 
	 */
	public void deleteAllDocuments();

	public Integer findClicksById(String id);
}
