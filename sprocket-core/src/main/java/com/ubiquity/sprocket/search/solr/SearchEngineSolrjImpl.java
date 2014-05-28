package com.ubiquity.sprocket.search.solr;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.sprocket.domain.Document;
import com.ubiquity.sprocket.search.SearchEngine;

public class SearchEngineSolrjImpl implements SearchEngine {

	private HttpSolrServer server;

	private Logger log = LoggerFactory.getLogger(getClass());

	public SearchEngineSolrjImpl(Configuration config) {
		server = new HttpSolrServer(config.getString("solr.api.path"));
	}

	@Override
	public void addDocument(Document document) {
		SolrInputDocument solrDoc = SolrApiDtoAssembler.assemble(document);
		try {
			server.add(solrDoc);
			server.commit();
		} catch (SolrServerException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void addDocuments(List<Document> documents) {

		if(documents.isEmpty()) {
			log.debug("Documents empty, nothing to add to search index");
			return;
		}
		
		List<SolrInputDocument> solrDocs = new LinkedList<SolrInputDocument>();
		for(Document document : documents) {
			SolrInputDocument solrDoc = SolrApiDtoAssembler.assemble(document);
			solrDocs.add(solrDoc);
			log.debug("indexing {}", solrDoc);
		}

		try {
			server.add(solrDocs);
			server.commit();
		} catch (SolrServerException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public List<Document> searchDocuments(String searchTerm, String[] fields, Map<String, Object> filters) {
		List<Document> documents = new LinkedList<Document>();

		// assemble query with filters
		SolrQuery query = new SolrQuery();
		query.setQuery(createQueryString(searchTerm, fields));
		query.setFilterQueries(createFilterArguments(filters));
		// do the search
		SolrDocumentList results = search(query);

		// add to return list
		for (SolrDocument result : results)
			documents.add(SolrApiDtoAssembler.assemble(result));
		return documents;
	}

	/**
	 * Creates a Solr query string from the input fields
	 * 
	 * @param fields
	 * 
	 * @return
	 */
	private String createQueryString(String searchTerm, String[] fields) {

		StringBuilder queryBuilder = new StringBuilder("{!type=dismax qf='");
		int i;
		for(i = 0; i < fields.length - 1; i++)
			queryBuilder.append(fields[i]).append(" "); 
		queryBuilder.append(fields[i]).append("'}").append(searchTerm);

		log.debug("solr query string: {}", queryBuilder.toString());
		return queryBuilder.toString();
	}
	
	private String[] createFilterArguments(Map<String, Object> filter) {
		String[] filters = new String[filter.size()];
		int i = 0;
		for(String key : filter.keySet()) {
			Object value = filter.get(key);
			filters[i] = key + ":" + value;
			i++;
		}
		return filters;
	}

	/***
	 * Returns a result list from a solr query
	 * @param query
	 * @return
	 */
	private SolrDocumentList search(SolrQuery query) {
		QueryResponse response;
		try {
			response = server.query(query);
		} catch (SolrServerException e) {
			throw new RuntimeException(e);
		}
		SolrDocumentList results = response.getResults();
		return results;
	}

	@Override
	public void deleteAllDocuments() {
		try {
			server.deleteByQuery("*:*");
		} catch (SolrServerException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}

}
