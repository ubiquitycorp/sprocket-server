package com.ubiquity.sprocket.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.domain.Message;
import com.ubiquity.sprocket.domain.Document;
import com.ubiquity.content.domain.VideoContent;
import com.ubiquity.sprocket.search.SearchEngine;
import com.ubiquity.sprocket.search.SearchKeys;
import com.ubiquity.sprocket.search.solr.SearchEngineSolrjImpl;

public class SearchService {
	
	private Logger log = LoggerFactory.getLogger(getClass());

	
	private SearchEngine searchEngine;
	
	public SearchService(Configuration config) {
		log.debug("Using solr api path: {}", config.getProperty("solr.api.path"));
		searchEngine = new SearchEngineSolrjImpl(config);
	}
	
	/***
	 * Adds a list of message entities to the search index for the owner of the message
	 * 
	 * @param messages
	 */
	public void indexMessages(List<Message> messages) {
		List<Document> documents = new LinkedList<Document>();
		for(Message message : messages) {
			
			if(message == null)
				continue; // TODO: remove this once the core issue is resolved
			
			Document document = new Document();
			
			document.getFields().put(SearchKeys.MessageContentFields.FIELD_SENDER, message.getSender().getDisplayName());
			document.getFields().put(SearchKeys.CommonFields.FIELD_BODY, message.getBody());
			document.getFields().put(SearchKeys.CommonFields.FIELD_TITLE, message.getTitle());

			document.getFields().put(SearchKeys.CommonFields.FIELD_DATA_TYPE, Message.class.getSimpleName());
			document.getFields().put(SearchKeys.CommonFields.FIELD_USER_ID, message.getOwner().getUserId());
			
		
			document.getFields().put(SearchKeys.CommonFields.FIELD_ID, message.getMessageId());

			
			documents.add(document);
		}
		
		addDocuments(documents);
	}
	
	/***
	 * Adds a list of activity entities to the search index for the owner of the activity
	 * 
	 * @param messages
	 */
	public void indexActivities(List<Activity> activities) {
		List<Document> documents = new LinkedList<Document>();
		for(Activity activity : activities) {
			
			Document document = new Document();
			
			document.getFields().put(SearchKeys.ActivityContentFields.FIELD_POSTED_BY, activity.getPostedBy().getDisplayName());
			document.getFields().put(SearchKeys.CommonFields.FIELD_BODY, activity.getBody());
			document.getFields().put(SearchKeys.CommonFields.FIELD_TITLE, activity.getTitle());
			document.getFields().put(SearchKeys.CommonFields.FIELD_DATA_TYPE, Activity.class.getSimpleName());
			document.getFields().put(SearchKeys.CommonFields.FIELD_USER_ID, activity.getOwner().getUserId());
			
			document.getFields().put(SearchKeys.CommonFields.FIELD_ID, activity.getActivityId()); 
			
			documents.add(document);
		}
		
		addDocuments(documents);
	}
	
	/***
	 * Adds a list of video entities to the search index for this user
	 * 
	 * @param videos
	 * @param userId
	 */
	public void indexVideos(List<VideoContent> videos, Long userId) {
		
		List<Document> documents = new LinkedList<Document>();
		for(VideoContent videoContent : videos) {
			
			Document document = new Document();
			
			document.getFields().put(SearchKeys.CommonFields.FIELD_TITLE, videoContent.getTitle());
			document.getFields().put(SearchKeys.CommonFields.FIELD_DESCRIPTION, videoContent.getDescription());
			
			if(videoContent.getThumb() != null)
				document.getFields().put(SearchKeys.CommonFields.FIELD_THUMBNAIL, videoContent.getThumb().getUrl());

			document.getFields().put(SearchKeys.VideoContentFields.FIELD_CATEGORY, videoContent.getCategory());
			document.getFields().put(SearchKeys.VideoContentFields.FIELD_ITEM_KEY, videoContent.getVideo().getItemKey());
			
			document.getFields().put(SearchKeys.CommonFields.FIELD_DATA_TYPE, VideoContent.class.getSimpleName());
			document.getFields().put(SearchKeys.CommonFields.FIELD_USER_ID, userId);
			document.getFields().put(SearchKeys.CommonFields.FIELD_ID, videoContent.getTitle().hashCode());
			
			documents.add(document);
		}
		
		addDocuments(documents);
	}
	
	public void addDocument(Document document) {
		searchEngine.addDocument(document);
	}
	
	public void addDocuments(List<Document> documents) {
		searchEngine.addDocuments(documents);
	}
	
	public List<Document> searchDocuments(String searchTerm, Long userId) {
		Map<String, Object> fields = new HashMap<String, Object>();
		
		// common keys
		fields.put(SearchKeys.CommonFields.FIELD_TITLE, searchTerm);
		fields.put(SearchKeys.CommonFields.FIELD_DESCRIPTION, searchTerm);
		fields.put(SearchKeys.CommonFields.FIELD_BODY, searchTerm);

		// video keys
		fields.put(SearchKeys.VideoContentFields.FIELD_CATEGORY, searchTerm);

		// message keys
		fields.put(SearchKeys.MessageContentFields.FIELD_SENDER, searchTerm);

		// activity keys
		fields.put(SearchKeys.ActivityContentFields.FIELD_POSTED_BY, searchTerm);

		// filters
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(SearchKeys.CommonFields.FIELD_USER_ID, userId);
		return searchEngine.searchDocuments(searchTerm, new String[] {
				SearchKeys.CommonFields.FIELD_TITLE,
				SearchKeys.CommonFields.FIELD_DESCRIPTION,
				SearchKeys.CommonFields.FIELD_BODY,
				SearchKeys.VideoContentFields.FIELD_CATEGORY,
				SearchKeys.MessageContentFields.FIELD_SENDER,
				SearchKeys.ActivityContentFields.FIELD_POSTED_BY
		}, filters);
	}

	public void deleteAll() {
		searchEngine.deleteAllDocuments();
		
	}
}
