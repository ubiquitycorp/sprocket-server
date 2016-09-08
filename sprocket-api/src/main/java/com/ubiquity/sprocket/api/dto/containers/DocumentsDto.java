package com.ubiquity.sprocket.api.dto.containers;

import java.util.LinkedList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.ubiquity.sprocket.api.dto.model.DocumentDto;
import com.ubiquity.sprocket.api.validation.EngagementValidation;

/**
 * Dto container class wrapping a list of documents with pagination
 * @author chris
 *
 */
public class DocumentsDto {
	
	@NotNull(groups = { EngagementValidation.class })
	private String searchTerm;
	
	@NotNull(groups = { EngagementValidation.class })
	@Size(min = 1, groups = { EngagementValidation.class })
	private List<DocumentDto> documents = new LinkedList<DocumentDto>();

	
	public DocumentsDto(String searchTerm) {
		super();
		this.searchTerm = searchTerm;
	}

	public List<DocumentDto> getDocuments() {
		return documents;
	}

	public String getSearchTerm() {
		return searchTerm;
	}
	
	

}
