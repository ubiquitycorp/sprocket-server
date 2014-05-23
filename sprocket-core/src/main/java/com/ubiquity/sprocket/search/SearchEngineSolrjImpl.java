package com.ubiquity.sprocket.search;

import java.io.IOException;
import java.util.Collection;
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

import com.ubiquity.sprocket.domain.Document;

public class SearchEngineSolrjImpl implements SearchEngine {

	private HttpSolrServer server;
	
	
	public SearchEngineSolrjImpl(Configuration config) {
		server = new HttpSolrServer(config.getString("solr.api.path"));
	}

	@Override
	public void addDocument(Document document) {
		
		SolrInputDocument doc = new SolrInputDocument();
		Map<String, Object> fields = document.getFields();
		for(String key : fields.keySet()) {
			doc.addField(key, fields.get(key));
		}
	    try {
			server.add(doc);
			server.commit();
		} catch (SolrServerException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Document> searchDocuments(String searchTerm, Long userId) {
		
		List<Document> documents = new LinkedList<Document>();
		
		SolrQuery query = new SolrQuery();
	    query.setQuery("general_content:"+searchTerm + " AND user_id:" + userId); 
	    QueryResponse response;
		try {
			response = server.query(query);
		} catch (SolrServerException e) {
			throw new RuntimeException(e);
		}
	    SolrDocumentList results = response.getResults();
	    for (int i = 0; i < results.size(); ++i) {
	    	SolrDocument result = results.get(i);
	    	Document document = new Document();
	    	Collection<String> fields = result.getFieldNames();
	    	for(String key : fields) {
	    		Object value = result.get(key);
	    		document.getFields().put(key, value);
	    	}
	    	documents.add(document);
	    }
	    return documents;
	}

}
