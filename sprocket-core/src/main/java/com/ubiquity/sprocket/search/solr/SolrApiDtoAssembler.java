package com.ubiquity.sprocket.search.solr;

import java.util.Collection;
import java.util.Map;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

import com.ubiquity.sprocket.domain.Document;
import com.ubiquity.sprocket.search.SearchKeys;

public class SolrApiDtoAssembler {
	
	/***
	 * Assembles a Solr input document from a Ubiquity domain search entity
	 * 
	 * @param document
	 * @return
	 */
	public static SolrInputDocument assemble(Document document) {
		SolrInputDocument solrDoc = new SolrInputDocument();
		Map<String, Object> fields = document.getFields();
		for(String key : fields.keySet()) {
			solrDoc.addField(key, fields.get(key));
		}
		return solrDoc;
	}
	
	/***
	 * Assembles a Ubiquity domain search entity from a Solr document 
	 * 
	 * @param solrDocument
	 * @return
	 */
	public static Document assemble(SolrDocument solrDocument) {
		Document document = new Document((String)solrDocument.get(SearchKeys.Fields.FIELD_DATA_TYPE));
		Collection<String> fieldNames = solrDocument.getFieldNames();
		for(String key : fieldNames) {
			Object value = solrDocument.get(key);
			document.getFields().put(key, value);
		}
		return document;
	}
	
	
}

