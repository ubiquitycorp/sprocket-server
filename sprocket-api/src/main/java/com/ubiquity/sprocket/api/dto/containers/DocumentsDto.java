package com.ubiquity.sprocket.api.dto.containers;

import java.util.LinkedList;
import java.util.List;

import com.ubiquity.sprocket.api.dto.model.DocumentDto;

/**
 * Dto container class wrapping a list of documents with pagination
 * @author chris
 *
 */
public class DocumentsDto {
	
	private List<DocumentDto> documents = new LinkedList<DocumentDto>();

	public List<DocumentDto> getDocuments() {
		return documents;
	}	

}
