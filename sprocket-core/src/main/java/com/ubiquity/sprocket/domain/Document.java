package com.ubiquity.sprocket.domain;

import java.util.HashMap;
import java.util.Map;

/***
 * Domain entity abstracting a search document. It can contain fields which can be serialized into a search db,
 * or a reference to the data
 * 
 * @author chris
 *
 */
public class Document {
	
	private String dataType;
	
	/**
	 * Fields to be stored in a search database
	 */
	private Map<String, Object> fields = new HashMap<String, Object>();
	
	/**
	 * Instance of the data
	 */
	private Object data;
	
	/***
	 * Search rank
	 */
	private Integer rank;
	
	
	/***
	 * Creates a document with required property data type
	 * 
	 * @param dataType
	 */
	public Document(String dataType) {
		this.dataType = dataType;
	}
	
	/***
	 * Parameterized constructor creates a document with data and rank
	 * 
	 * @param data
	 * @param rank
	 */
	public Document(String dataType, Object data, Integer rank) {
		this.dataType = dataType;
		this.data = data;
		this.rank = rank;
	}

	
	public String getDataType() {
		return dataType;
	}

	public Object getData() {
		return data;
	}

	public Integer getRank() {
		return rank;
	}


	public Map<String, Object> getFields() {
		return fields;
	}

	@Override
	public String toString() {
		return "Document [fields=" + fields + "]";
	}
	
	

}
