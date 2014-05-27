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
import com.ubiquity.social.domain.VideoContent;
import com.ubiquity.sprocket.domain.Document;
import com.ubiquity.sprocket.search.SearchEngine;
import com.ubiquity.sprocket.search.SearchEngineSolrjImpl;

public class SearchService {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	public static class Keys {
		
		public static class CommonFields {
			public static final String FIELD_TITLE = "title";
			public static final String FIELD_DESCRIPTION = "description";
			public static final String FIELD_USER_ID = "user_id";
			public static final String FIELD_ID = "id";
			public static final String FIELD_DATA_TYPE = "data_type";
			public static final String FIELD_BODY = "body";
		}
		
		public static class VideoContentFields {
			public static final String FIELD_CATEGORY = "category";
		}
		
		public static class MessageContentFields {
			public static final String FIELD_SENDER = "sender";
		}
		
		public static class ActivityContentFields {
			public static final String FIELD_POSTED_BY = "posted_by";
		}
	}
	
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
			
			Document document = new Document();
			
			document.getFields().put(Keys.MessageContentFields.FIELD_SENDER, message.getSender().getDisplayName());
			document.getFields().put(Keys.CommonFields.FIELD_BODY, message.getBody());

			document.getFields().put(Keys.CommonFields.FIELD_DATA_TYPE, Message.class.getSimpleName());
			document.getFields().put(Keys.CommonFields.FIELD_USER_ID, message.getOwner().getUserId());
			document.getFields().put(Keys.CommonFields.FIELD_ID, message.getMessageId());
			
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
			
			document.getFields().put(Keys.ActivityContentFields.FIELD_POSTED_BY, activity.getPostedBy().getDisplayName());
			document.getFields().put(Keys.CommonFields.FIELD_BODY, activity.getBody());

			document.getFields().put(Keys.CommonFields.FIELD_DATA_TYPE, Activity.class.getSimpleName());
			document.getFields().put(Keys.CommonFields.FIELD_USER_ID, activity.getOwner().getUserId());
			document.getFields().put(Keys.CommonFields.FIELD_ID, activity.getActivityId());
			
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
			
			document.getFields().put(Keys.CommonFields.FIELD_TITLE, videoContent.getTitle());
			document.getFields().put(Keys.CommonFields.FIELD_DESCRIPTION, videoContent.getDescription());
			document.getFields().put(Keys.VideoContentFields.FIELD_CATEGORY, videoContent.getCategory());

			document.getFields().put(Keys.CommonFields.FIELD_DATA_TYPE, VideoContent.class.getSimpleName());
			document.getFields().put(Keys.CommonFields.FIELD_USER_ID, userId);
			document.getFields().put(Keys.CommonFields.FIELD_ID, videoContent.getVideoContentId());
			
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
		fields.put(Keys.CommonFields.FIELD_TITLE, searchTerm);
		fields.put(Keys.CommonFields.FIELD_DESCRIPTION, searchTerm);
		fields.put(Keys.CommonFields.FIELD_BODY, searchTerm);

		// video keys
		fields.put(Keys.VideoContentFields.FIELD_CATEGORY, searchTerm);

		// message keys
		fields.put(Keys.MessageContentFields.FIELD_SENDER, searchTerm);

		// activity keys
		fields.put(Keys.ActivityContentFields.FIELD_POSTED_BY, searchTerm);

		// filters
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(Keys.CommonFields.FIELD_USER_ID, userId);
		return searchEngine.searchDocuments(searchTerm, new String[] {
				Keys.CommonFields.FIELD_TITLE,
				Keys.CommonFields.FIELD_DESCRIPTION,
				Keys.CommonFields.FIELD_BODY,
				Keys.VideoContentFields.FIELD_CATEGORY,
				Keys.MessageContentFields.FIELD_SENDER,
				Keys.ActivityContentFields.FIELD_POSTED_BY
		}, filters);
	}

	public void deleteAll() {
		searchEngine.deleteAllDocuments();
		
	}
}
