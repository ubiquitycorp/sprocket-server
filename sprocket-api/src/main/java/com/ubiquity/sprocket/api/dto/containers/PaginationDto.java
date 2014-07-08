package com.ubiquity.sprocket.api.dto.containers;

public class PaginationDto {
	
	private Boolean hasNextPage = Boolean.FALSE;

	public Boolean getHasNextPage() {
		return hasNextPage;
	}

	public void setHasNextPage(Boolean hasNextPage) {
		this.hasNextPage = hasNextPage;
	}

}
