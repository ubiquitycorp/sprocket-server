package com.ubiquity.sprocket.domain;

import java.util.HashMap;
import java.util.Map;

public class Document {
	
	private Map<String, Object> fields = new HashMap<String, Object>();

	public Map<String, Object> getFields() {
		return fields;
	}

	@Override
	public String toString() {
		return "Document [fields=" + fields + "]";
	}
	
	

}
