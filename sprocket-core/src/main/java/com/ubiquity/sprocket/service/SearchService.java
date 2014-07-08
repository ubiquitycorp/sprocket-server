package com.ubiquity.sprocket.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.utils.Page;
import com.ubiquity.content.domain.ContentNetwork;
import com.ubiquity.content.domain.VideoContent;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.social.api.SocialAPI;
import com.ubiquity.social.api.SocialAPIFactory;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.domain.Message;
import com.ubiquity.social.domain.SocialNetwork;
import com.ubiquity.social.service.SocialService;
import com.ubiquity.sprocket.domain.Document;
import com.ubiquity.sprocket.search.SearchEngine;
import com.ubiquity.sprocket.search.SearchKeys;
import com.ubiquity.sprocket.search.solr.SearchEngineSolrjImpl;

/***
 * Search service encapsulates all search functions for both indexed searches and live searches
 * 
 * @author chris
 *
 */
public class SearchService {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	private SearchEngine searchEngine;
	
	private Integer resultsLimit;
	private Integer pageLimit;
	
	public SearchService(Configuration config) {
		log.debug("Using solr api path: {}", config.getProperty("solr.api.path"));
		resultsLimit = config.getInt("rules.search.results.limit");
		pageLimit = config.getInt("rules.search.results.page.limit");
				
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

			Document document = new Document(Message.class.getName());

			document.getFields().put(SearchKeys.Fields.FIELD_SENDER, message.getSender().getDisplayName());
			document.getFields().put(SearchKeys.Fields.FIELD_BODY, message.getBody());
			document.getFields().put(SearchKeys.Fields.FIELD_TITLE, message.getTitle());

			document.getFields().put(SearchKeys.Fields.FIELD_DATA_TYPE, Message.class.getSimpleName());
			document.getFields().put(SearchKeys.Fields.FIELD_USER_ID, message.getOwner().getUserId());
			document.getFields().put(SearchKeys.Fields.FIELD_SOCIAL_NETWORK_ID, message.getSocialNetwork().ordinal());
			document.getFields().put(SearchKeys.Fields.FIELD_ID, message.getMessageId());


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

			Document document = new Document(Activity.class.getName());

			document.getFields().put(SearchKeys.Fields.FIELD_POSTED_BY, activity.getPostedBy().getDisplayName());
			document.getFields().put(SearchKeys.Fields.FIELD_BODY, activity.getBody());
			document.getFields().put(SearchKeys.Fields.FIELD_TITLE, activity.getTitle());
			document.getFields().put(SearchKeys.Fields.FIELD_DATA_TYPE, Activity.class.getSimpleName());
			document.getFields().put(SearchKeys.Fields.FIELD_USER_ID, activity.getOwner().getUserId());
			document.getFields().put(SearchKeys.Fields.FIELD_SOCIAL_NETWORK_ID, activity.getSocialNetwork().ordinal());
			document.getFields().put(SearchKeys.Fields.FIELD_ID, activity.getActivityId()); 

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

			Document document = new Document(VideoContent.class.getSimpleName());

			document.getFields().put(SearchKeys.Fields.FIELD_TITLE, videoContent.getTitle());
			document.getFields().put(SearchKeys.Fields.FIELD_DESCRIPTION, videoContent.getDescription());

			if(videoContent.getThumb() != null)
				document.getFields().put(SearchKeys.Fields.FIELD_THUMBNAIL, videoContent.getThumb().getUrl());

			document.getFields().put(SearchKeys.Fields.FIELD_CATEGORY, videoContent.getCategory());
			document.getFields().put(SearchKeys.Fields.FIELD_ITEM_KEY, videoContent.getVideo().getItemKey());

			document.getFields().put(SearchKeys.Fields.FIELD_DATA_TYPE, VideoContent.class.getSimpleName());
			document.getFields().put(SearchKeys.Fields.FIELD_USER_ID, userId);
			document.getFields().put(SearchKeys.Fields.FIELD_ID, videoContent.getTitle().hashCode());

			documents.add(document);
		}

		addDocuments(documents);
	}

	/***
	 * Indexes a document
	 * 
	 * @param document
	 */
	public void addDocument(Document document) {
		searchEngine.addDocument(document);
	}

	/***
	 * Indexes a batch of documents
	 * 
	 * @param documents
	 */
	public void addDocuments(List<Document> documents) {
		searchEngine.addDocuments(documents);
	}


	/***
	 * Searches public activities for a social network
	 * 
	 * @param searchTerm
	 * @param userId
	 * @param socialNetwork
	 * @return
	 */
	public List<Document> searchLiveActivities(String searchTerm, User user, SocialNetwork socialNetwork, ClientPlatform clientPlatform, Integer page) {

		List<Document> documents = new LinkedList<Document>();
		
		// get the identity and social network
		ExternalIdentity identity = SocialService.getAssociatedSocialIdentity(user, socialNetwork);
		SocialAPI socialAPI = SocialAPIFactory.createProvider(socialNetwork, clientPlatform);
		
		// calculate offset with page utility based on page limits
		int offset = Page.calculateOffsetFromPage(page, resultsLimit, pageLimit);
		List<Activity> activities = socialAPI.searchActivities(identity, searchTerm, resultsLimit, offset);
		
		// now wrap them in a search document
		int rank = 0;
		for(Activity activity : activities) {
			Document document = new Document(activity.getClass().getSimpleName(), activity, rank);
			rank++;
			documents.add(document);
		}
		
		log.debug("documents {}", documents);

		return documents;
		
	}
	
	/***
	 * Searches documents for a social network
	 * 
	 * @param searchTerm
	 * @param userId
	 * @param socialNetwork
	 * @return
	 */
	public List<Document> searchIndexedDocuments(String searchTerm, Long userId, SocialNetwork socialNetwork) {

		// filters
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(SearchKeys.Fields.FIELD_USER_ID, userId);
		filters.put(SearchKeys.Fields.FIELD_SOCIAL_NETWORK_ID, socialNetwork.ordinal());
		
		return searchEngine.searchDocuments(searchTerm, createFieldsToSearchOver(), filters);

	}
	
	/***
	 * Searches documents for a content network
	 * 
	 * @param searchTerm
	 * @param userId
	 * @param contentNetwork
	 * @return
	 */
	public List<Document> searchIndexedDocuments(String searchTerm, Long userId, ContentNetwork socialNetwork) {

		// filters
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(SearchKeys.Fields.FIELD_USER_ID, userId);
		filters.put(SearchKeys.Fields.FIELD_SOCIAL_NETWORK_ID, socialNetwork.ordinal());
		
		return searchEngine.searchDocuments(searchTerm, createFieldsToSearchOver(), filters);

	}
	

	/***
	 * Searches documents by a search term and user id across all social networks and content providers
	 * 
	 * @param searchTerm
	 * @param userId
	 * @return
	 */
	public List<Document> searchIndexedDocuments(String searchTerm, Long userId) {

		// filters
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(SearchKeys.Fields.FIELD_USER_ID, userId);
		return searchEngine.searchDocuments(searchTerm, createFieldsToSearchOver(), filters);
	}

	/***
	 * Clears entire search index
	 */
	public void deleteAll() {
		searchEngine.deleteAllDocuments();

	}

	private String[] createFieldsToSearchOver() {
		return new String[] {
				SearchKeys.Fields.FIELD_TITLE,
				SearchKeys.Fields.FIELD_DESCRIPTION,
				SearchKeys.Fields.FIELD_BODY,
				SearchKeys.Fields.FIELD_SENDER,
				SearchKeys.Fields.FIELD_POSTED_BY,
				SearchKeys.Fields.FIELD_CATEGORY
		};
	}


}
