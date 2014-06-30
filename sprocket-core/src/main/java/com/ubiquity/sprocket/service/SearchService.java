package com.ubiquity.sprocket.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.content.domain.ContentNetwork;
import com.ubiquity.content.domain.VideoContent;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.domain.Message;
import com.ubiquity.social.domain.SocialNetwork;
import com.ubiquity.sprocket.domain.Document;
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


	/***
	 * Searches documents for a social network
	 * 
	 * @param searchTerm
	 * @param userId
	 * @param socialNetwork
	 * @return
	 */
	public List<Document> searchDocuments(String searchTerm, Long userId, SocialNetwork socialNetwork) {

		// filters
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(SearchKeys.CommonFields.FIELD_USER_ID, userId);
		filters.put(SearchKeys.CommonFields.FIELD_SOCIAL_NETWORK_ID, socialNetwork.ordinal());
		
		return searchEngine.searchDocuments(searchTerm, createFieldsToSearchForSocialNetwork(), filters);

	}
	
	/***
	 * Searches documents for a content network
	 * 
	 * @param searchTerm
	 * @param userId
	 * @param contentNetwork
	 * @return
	 */
	public List<Document> searchDocuments(String searchTerm, Long userId, ContentNetwork socialNetwork) {

		// filters
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(SearchKeys.CommonFields.FIELD_USER_ID, userId);
		filters.put(SearchKeys.CommonFields.FIELD_SOCIAL_NETWORK_ID, socialNetwork.ordinal());
		
		return searchEngine.searchDocuments(searchTerm, createFieldsToSearchForSocialNetwork(), filters);

	}
	
	
	

	/***
	 * Searches documents by a search term and user id across all social entworks and content providers
	 * 
	 * @param searchTerm
	 * @param userId
	 * @return
	 */
	public List<Document> searchDocuments(String searchTerm, Long userId) {

		// filters
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(SearchKeys.CommonFields.FIELD_USER_ID, userId);
		return searchEngine.searchDocuments(searchTerm, createFieldsToSearchForAllNetworks(), filters);
	}

	public void deleteAll() {
		searchEngine.deleteAllDocuments();

	}

	/***
	 * Creates a field map to search over for a social network
	 * @param searchTerm
	 * @param socialNetwork
	 * @return
	 */
	private Map<String, Object> createSearchFieldsForSocialNetwork(String searchTerm) {

		Map<String, Object> fields = new HashMap<String, Object>();


		// common keys
		fields.put(SearchKeys.CommonFields.FIELD_TITLE, searchTerm);
		fields.put(SearchKeys.CommonFields.FIELD_DESCRIPTION, searchTerm);
		fields.put(SearchKeys.CommonFields.FIELD_BODY, searchTerm);

		// message keys
		fields.put(SearchKeys.MessageContentFields.FIELD_SENDER, searchTerm);

		// activity keys
		fields.put(SearchKeys.ActivityContentFields.FIELD_POSTED_BY, searchTerm);

		return fields;
	}

	private String[] createFieldsToSearchForSocialNetwork() {
		return new String[] {
				SearchKeys.CommonFields.FIELD_TITLE,
				SearchKeys.CommonFields.FIELD_DESCRIPTION,
				SearchKeys.CommonFields.FIELD_BODY,
				SearchKeys.MessageContentFields.FIELD_SENDER,
				SearchKeys.ActivityContentFields.FIELD_POSTED_BY
		};
	}

	private String[] createFieldsToSearchForContentNetwork() {
		return new String[] {
				SearchKeys.VideoContentFields.FIELD_CATEGORY
		};
	}

	private String[] createFieldsToSearchForAllNetworks() {
		List<String> list = new LinkedList<String>();
		for(String field : createFieldsToSearchForSocialNetwork())
			list.add(field);
		for(String field : createFieldsToSearchForContentNetwork())
			list.add(field);

		return (String[])list.toArray();
	}


}
