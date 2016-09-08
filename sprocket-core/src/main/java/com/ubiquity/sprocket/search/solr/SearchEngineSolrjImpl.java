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
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.sprocket.domain.Document;
import com.ubiquity.sprocket.search.SearchEngine;
import com.ubiquity.sprocket.search.SearchKeys;

public class SearchEngineSolrjImpl implements SearchEngine {

	private HttpSolrServer server;

	private Logger log = LoggerFactory.getLogger(getClass());

	public SearchEngineSolrjImpl(Configuration config) {
		server = new HttpSolrServer(config.getString("solr.api.path"));
	}

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

	public List<Document> searchDocuments(String searchTerm, String[] fields, Map<String, Object> filters, SolrOperator operator) {
		List<Document> documents = new LinkedList<Document>();

		// assemble query with filters
		SolrQuery query = new SolrQuery();
		query.setQuery(createQueryString(searchTerm, fields, operator));
		query.setFilterQueries(createFilterArguments(filters, operator));
		// do the search
		SolrDocumentList results = search(query, false);

		// add to return list
		for (SolrDocument result : results)
			documents.add(SolrApiDtoAssembler.assemble(result));
		return documents;
	}
	
	public List<Document> searchDocuments(String searchTerm, String[] fields, Map<String, Object> filters, 
											SolrOperator operator, String groupField, Integer groupLimit, Integer resultsLimit) {
		List<Document> documents = new LinkedList<Document>();

		// assemble query with filters
		SolrQuery query = new SolrQuery();
		query.setQuery(createQueryString(searchTerm, fields, operator));
		query.setFilterQueries(createFilterArguments(filters, operator));
		query.setParam("group", true);
		query.setParam("group.field",groupField);
		query.setParam("group.limit",groupLimit.toString());
		query.setParam("group.format", "simple");
		query.setRows(resultsLimit);
		// do the search
		SolrDocumentList results = search(query, true);

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
	private String createQueryString(String searchTerm, String[] fields, SolrOperator operator) {

		StringBuilder queryBuilder = new StringBuilder("{!type=dismax q.op=" + operator.name() + " qf='");
		int i;
		for(i = 0; i < fields.length - 1; i++)
			queryBuilder.append(fields[i]).append(" "); 
		queryBuilder.append(fields[i]).append("'}").append(searchTerm);

		log.debug("solr query string: {}", queryBuilder.toString());
		return queryBuilder.toString();
	}
	
	private String[] createFilterArguments(Map<String, Object> filter, SolrOperator operator) {
	
		String[] filters = new String[filter.size()];
		int i = 0;
		for(String key : filter.keySet()) {
			Object value = filter.get(key);
			if(value instanceof List){
				@SuppressWarnings("unchecked")
				List<Object> values = (List<Object>) value;
				filters[i] = "";
				//Add multi-values separated by spaces as key:value pair
				for (Object object : values) {
					filters[i] += key + ":" + object + " ";
				}
			} else
				filters[i] = key + ":" + value + " ";
			// Only add new filter entity if it is AND condition
			if(operator.equals(SolrOperator.AND))
				i++;
		}
	
		for(String argument : filters) {
			log.debug("solr filter arg: {}", argument);
		}
		return filters;
	}

	/***
	 * Returns a result list from a solr query
	 * @param query
	 * @return
	 */
	private SolrDocumentList search(SolrQuery query, Boolean isGroupResult) {
		QueryResponse response;
		try {
			response = server.query(query);
		} catch (SolrServerException e) {
			throw new RuntimeException(e);
		}
		SolrDocumentList results;
		if(isGroupResult)
			results = response.getGroupResponse().getValues().get(0).getValues().get(0).getResult();
		else
			results = response.getResults();
		return results;
	}

	public void deleteAllDocuments() {
		try {
			server.deleteByQuery("*:*");
		} catch (SolrServerException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}

	@Override
	public Integer findClicksById(String id) {
		QueryResponse response;
		try {
			SolrQuery query = new SolrQuery();
			query.setQuery(SearchKeys.Fields.FIELD_ID + ":" + ClientUtils.escapeQueryChars(id));
			
			query.setParam("fl", SearchKeys.Fields.FIELD_ID + "," + SearchKeys.Fields.FIELD_CLICKS);
			response = server.query(query);
		} catch (SolrServerException e) {
			throw new RuntimeException(e);
		}
		if(response.getResults().size() > 0){
			SolrDocument doc = response.getResults().get(0);
			if(doc.get(SearchKeys.Fields.FIELD_CLICKS) != null)
				return Integer.parseInt(doc.get(SearchKeys.Fields.FIELD_CLICKS).toString());
			else
				return 0;
		} else
			return 0;
	}

}
