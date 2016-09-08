package com.ubiquity.sprocket.api.dto.containers;

import java.util.LinkedList;
import java.util.List;

public class DataSyncedDto<T> {
	private List<T> added = new LinkedList<T>();
	
	private List<T> updated = new LinkedList<T>();
	
	private List<Long> deleted = new LinkedList<Long>();
	
	public List<T> getAdded() {
		return added;
	}
	public List<T> getUpdated() {
		return updated;
	}
	public List<Long> getDeleted() {
		return deleted;
	}
	
	
}
