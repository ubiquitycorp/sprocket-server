package com.ubiquity.sprocket.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

<<<<<<< HEAD
import com.niobium.repository.CollectionVariant;
import com.niobium.repository.cache.NestedMapCache;
import com.niobium.repository.cache.UserCacheRedisImpl;
import com.ubiquity.content.api.ContentAPI;
import com.ubiquity.content.api.ContentAPIFactory;
import com.ubiquity.content.domain.VideoContent;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.external.domain.Network;
import com.ubiquity.external.repository.cache.CacheKeys;
import com.ubiquity.external.service.ExternalIdentityService;
=======
import com.ubiquity.identity.domain.ClientPlatform;
>>>>>>> develop
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.integration.api.ContentAPI;
import com.ubiquity.integration.api.ContentAPIFactory;
import com.ubiquity.integration.api.PlaceAPI;
import com.ubiquity.integration.api.PlaceAPIFactory;
import com.ubiquity.integration.api.SocialAPI;
import com.ubiquity.integration.api.SocialAPIFactory;
import com.ubiquity.integration.domain.Activity;
import com.ubiquity.integration.domain.ActivityType;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.Message;
import com.ubiquity.integration.domain.Network;
import com.ubiquity.integration.domain.VideoContent;
import com.ubiquity.integration.service.ExternalIdentityService;
import com.ubiquity.location.domain.Place;
import com.ubiquity.location.domain.UserLocation;
import com.ubiquity.location.repository.UserLocationRepository;
import com.ubiquity.location.repository.UserLocationRepositoryJpaImpl;
import com.ubiquity.media.domain.Image;
<<<<<<< HEAD
import com.ubiquity.social.api.SocialAPI;
import com.ubiquity.social.api.SocialAPIFactory;
import com.ubiquity.social.api.util.Pagination;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.domain.ActivityType;
import com.ubiquity.social.domain.Message;
=======
>>>>>>> develop
import com.ubiquity.sprocket.domain.Document;
import com.ubiquity.sprocket.search.SearchEngine;
import com.ubiquity.sprocket.search.SearchKeys;
import com.ubiquity.sprocket.search.solr.SearchEngineSolrjImpl;
import com.ubiquity.sprocket.search.solr.SolrOperator;

/***
 * Search service encapsulates all search functions for both indexed searches and live searches
 * 
 * @author chris
 *
 */
public class SearchService {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	private SearchEngine searchEngine;
	
<<<<<<< HEAD
	private Integer resultsLimit, pageLimit;
	private NestedMapCache requestStateCache;
=======
	private Integer resultsLimit, pageLimit, networkLimit, networkNumbers;
	
>>>>>>> develop
	public SearchService(Configuration config) {
		log.debug("Using solr api path: {}", config.getProperty("solr.api.path"));
		requestStateCache = new UserCacheRedisImpl(config.getInt("redis.external.uri.request.state"));
		resultsLimit = config.getInt("rules.search.results.limit");
		pageLimit = config.getInt("rules.search.page.limit");
		networkLimit = config.getInt("rules.search.network.limit");
		networkNumbers = config.getInt("networks.number");
		searchEngine = new SearchEngineSolrjImpl(config);
		
	}

	
	/***
	 * Adds a list of message entities to the search index for the owner of the message; this method automatically
	 * sets a user filter since all direct messages are private
	 * 
	 * @param messages
	 */
	public void indexMessages(Long userFilterId, List<Message> messages) {
		List<Document> documents = new LinkedList<Document>();
		for(Message message : messages) {

			if(message == null)
				continue; // TODO: remove this once the core issue is resolved
			
			String dataType = Message.class.getSimpleName();
			Document document = new Document(dataType);

			// create identifier from the user filter (or default), the pk of the entity, and the data type
			String id = SearchKeys.generateDocumentKeyForId(userFilterId, message.getMessageId(), dataType);
			document.getFields().put(SearchKeys.Fields.FIELD_ID, id);
			
			document.getFields().put(SearchKeys.Fields.FIELD_CONTACT_DISPLAY_NAME, message.getSender().getDisplayName());
			document.getFields().put(SearchKeys.Fields.FIELD_CONTACT_IDENTIFIER, message.getSender().getExternalIdentity().getIdentifier());
			
			Image thumb = message.getSender().getImage();
			if(thumb != null)
				document.getFields().put(SearchKeys.Fields.FIELD_CONTACT_THUMBNAIL, thumb.getUrl());
			document.getFields().put(SearchKeys.Fields.FIELD_OWNER_ID, message.getOwner().getUserId());

			document.getFields().put(SearchKeys.Fields.FIELD_BODY, message.getBody());
			document.getFields().put(SearchKeys.Fields.FIELD_TITLE, message.getTitle());

			document.getFields().put(SearchKeys.Fields.FIELD_DATA_TYPE, Message.class.getSimpleName());
			document.getFields().put(SearchKeys.Fields.FIELD_EXTERNAL_NETWORK_ID, message.getExternalNetwork().ordinal());

			document.getFields().put(SearchKeys.Fields.FIELD_CLICKS, 1);
			
			documents.add(document);
		}

		addDocuments(documents);
	}

	/***
	 * Adds a list of activity entities to the search index with a user filter
     *
	 * @param userFilterId
	 * @param activities
	 * @param isEngaged: indicates whether these activities are engaged or not
	 */
	public void indexActivities(Long userFilterId, List<Activity> activities, Boolean isEngaged) {
		List<Document> documents = new LinkedList<Document>();
		for(Activity activity : activities) {

			String dataType = Activity.class.getSimpleName();
			Document document = new Document(dataType);
			Integer clicks = 1;
			
			// create identifier from the user filter (or default), the pk of the entity, and the data type
			String id = SearchKeys.generateDocumentKeyForId(userFilterId, activity.getActivityId(), dataType);
			document.getFields().put(SearchKeys.Fields.FIELD_ID, id); 
						
			Long ownerId = SearchKeys.generateOwnerId(userFilterId);
			document.getFields().put(SearchKeys.Fields.FIELD_OWNER_ID, ownerId); 

			// contact, add image if we have one
			document.getFields().put(SearchKeys.Fields.FIELD_CONTACT_DISPLAY_NAME, activity.getPostedBy().getDisplayName());
			document.getFields().put(SearchKeys.Fields.FIELD_CONTACT_IDENTIFIER, activity.getPostedBy().getExternalIdentity().getIdentifier());
			Image thumb = activity.getPostedBy().getImage();
			if(thumb != null)
				document.getFields().put(SearchKeys.Fields.FIELD_CONTACT_THUMBNAIL, activity.getPostedBy().getImage().getUrl());
			
			// content fields
			document.getFields().put(SearchKeys.Fields.FIELD_BODY, activity.getBody());
			document.getFields().put(SearchKeys.Fields.FIELD_TITLE, activity.getTitle());
			
			ActivityType type = activity.getActivityType();
			// if it's a video, set the url and thumbnail to the video url and image respectively
			if(type == ActivityType.VIDEO) {
				document.getFields().put(SearchKeys.Fields.FIELD_URL, activity.getVideo().getUrl());
				// use image as thumb
				if(activity.getImage() != null)
					document.getFields().put(SearchKeys.Fields.FIELD_THUMBNAIL, activity.getImage().getUrl());
			} else if(type == ActivityType.PHOTO) {
				document.getFields().put(SearchKeys.Fields.FIELD_URL, activity.getImage().getUrl());
			} else if(type == ActivityType.LINK) {
				document.getFields().put(SearchKeys.Fields.FIELD_URL, activity.getLink());
			}

			document.getFields().put(SearchKeys.Fields.FIELD_ACTIVITY_TYPE, activity.getActivityType().toString());
			document.getFields().put(SearchKeys.Fields.FIELD_DATA_TYPE, Activity.class.getSimpleName());
			document.getFields().put(SearchKeys.Fields.FIELD_EXTERNAL_NETWORK_ID, activity.getExternalNetwork().ordinal());
			document.getFields().put(SearchKeys.Fields.FIELD_EXTERNAL_IDENTIFIER, activity.getExternalIdentifier());
			document.getFields().put(SearchKeys.Fields.FIELD_DATE, activity.getCreationDate());
			
			if(isEngaged){
				clicks = searchEngine.findClicksById(id);
				clicks++;
			}
			document.getFields().put(SearchKeys.Fields.FIELD_CLICKS, clicks);
			
			documents.add(document);
		}

		addDocuments(documents);
	}

	/***
	 * Adds a list of video entities to the search index for this user with an optional user filter
	 * 
	 * @param videos
	 * @param userId
	 */
	public void indexVideos(Long userId, List<VideoContent> videos, Boolean isEngaged) {

		List<Document> documents = new LinkedList<Document>();
		for(VideoContent videoContent : videos) {

			String dataType = VideoContent.class.getSimpleName();
			Document document = new Document(dataType);
			Integer clicks = 1;
			
			// create identifier from the user filter (or default), the pk of the entity, and the data type
			String id = SearchKeys.generateDocumentKeyForId(userId, videoContent.getVideoContentId(), dataType);
			document.getFields().put(SearchKeys.Fields.FIELD_ID, id);
			
			Long ownerId = SearchKeys.generateOwnerId(userId);
			document.getFields().put(SearchKeys.Fields.FIELD_OWNER_ID, ownerId);


			document.getFields().put(SearchKeys.Fields.FIELD_TITLE, videoContent.getTitle());
			document.getFields().put(SearchKeys.Fields.FIELD_DESCRIPTION, videoContent.getDescription());

			if(videoContent.getThumb() != null)
				document.getFields().put(SearchKeys.Fields.FIELD_THUMBNAIL, videoContent.getThumb().getUrl());

			document.getFields().put(SearchKeys.Fields.FIELD_CATEGORY, videoContent.getCategoryExternalIdentifier());
			document.getFields().put(SearchKeys.Fields.FIELD_ITEM_KEY, videoContent.getVideo().getItemKey());

			document.getFields().put(SearchKeys.Fields.FIELD_DATA_TYPE, VideoContent.class.getSimpleName());
			document.getFields().put(SearchKeys.Fields.FIELD_EXTERNAL_NETWORK_ID, videoContent.getExternalNetwork().ordinal());

			if(isEngaged){
				clicks = searchEngine.findClicksById(id);
				clicks++;
			}
			document.getFields().put(SearchKeys.Fields.FIELD_CLICKS, clicks);

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
	 * @param externalNetwork
	 * @return
	 */
	public List<Document> searchLiveDocuments(String searchTerm, User user, ExternalNetwork externalNetwork, Integer page) {

		// normalize page
		page = page == null ? 1 : page;
			
		// first check page limit
		if(page > pageLimit)
			throw new IllegalArgumentException("Page limit reached");
			
		List<Document> documents = null;
		// get the identity and social network
		ExternalIdentity identity = null;
		if(externalNetwork != ExternalNetwork.Yelp) 
			identity = ExternalIdentityService.getAssociatedExternalIdentity(user, externalNetwork);
		
		// if it's social, search activities only
		if(externalNetwork.getNetwork() == Network.Social) {
			SocialAPI socialAPI = SocialAPIFactory.createProvider(externalNetwork, identity.getClientPlatform());
			// calculate offset with page utility based on page limits
			List<Activity> activities = socialAPI.searchActivities(searchTerm, page, resultsLimit, identity);
			documents = wrapEntitiesInDocuments(activities);
			
<<<<<<< HEAD
		} else {
			List<VideoContent> videoContent = searchLiveVedios(searchTerm, identity, externalNetwork, page);
=======
		} else if(externalNetwork.getNetwork() == Network.Content){
			// if content, search videos
			ContentAPI contentAPI = ContentAPIFactory.createProvider(externalNetwork, identity.getClientPlatform());
			List<VideoContent> videoContent = contentAPI.searchVideos(searchTerm, page, resultsLimit, identity);
			
>>>>>>> develop
			documents = wrapEntitiesInDocuments(videoContent);
		}else {
			// if content, search videos
			PlaceAPI placeAPI = PlaceAPIFactory.createProvider(externalNetwork, ClientPlatform.WEB);
			UserLocationRepository repo = new UserLocationRepositoryJpaImpl();
			UserLocation location = repo.findByUserId(user.getUserId());
			if(location!= null)
			{
				List<Place> places = placeAPI.searchPlacesWithinPlace(searchTerm, location.getNearestPlace(), null, page, resultsLimit>20?20:resultsLimit);			
				documents = wrapEntitiesInDocuments(places);
			}
			else{
				throw new IllegalArgumentException("User doesn't set his locaion yet");
			}
		}

		return documents;
		
	}
	public List<VideoContent>  searchLiveVedios(String searchTerm, ExternalIdentity identity, ExternalNetwork externalNetwork, Integer page )
	{
		String pageToken = null;
		String cacheKey = null;
		if(externalNetwork == ExternalNetwork.YouTube)
		{
			// if content, search videos
			cacheKey = CacheKeys.generateCacheKeyForYouTubeSearchPagination(searchTerm, identity);
			// get the saved token state from the cache to determine the page token to send to YouTube
			pageToken = Pagination.getPageToken(requestStateCache, cacheKey, page);
		}
		ContentAPI contentAPI = ContentAPIFactory.createProvider(externalNetwork, identity.getClientPlatform());
		CollectionVariant<VideoContent> videoContent = contentAPI.searchVideos(searchTerm, page, resultsLimit,pageToken, identity);
		if(externalNetwork == ExternalNetwork.YouTube)
		{
			// set the cache state
			Pagination.setPageToken(requestStateCache, cacheKey, page, videoContent.getPrevPageToken(), videoContent.getPageToken(), videoContent.getNextPageToken());
		}
		return (List<VideoContent>) videoContent.collection;
	}
	
	/***
	 * Wrap entities in documents
	 * 
	 * @param entities
	 * @return
	 */
	private <T> List<Document> wrapEntitiesInDocuments(List<T> entities) {
		List<Document> documents = new LinkedList<Document>();

		int rank = 0;
		for(T entity : entities) {
			Document document = new Document(entity.getClass().getSimpleName(), entity, rank);
			rank++;
			documents.add(document);
		}
		return documents;
	}
	
	
	/***
	 * Searches documents for a social network
	 * 
	 * @param searchTerm
	 * @param userIdFilter
	 * @param socialNetwork
	 * @return
	 */
	public List<Document> searchIndexedDocuments(String searchTerm, Long userIdFilter, ExternalNetwork externalNetwork) {

		// filters
		Map<String, Object> filters = new HashMap<String, Object>();

		filters.put(SearchKeys.Fields.FIELD_EXTERNAL_NETWORK_ID, externalNetwork.ordinal());
		Long ownerId = SearchKeys.generateOwnerId(userIdFilter);
		filters.put(SearchKeys.Fields.FIELD_OWNER_ID, ownerId);
		
		return searchEngine.searchDocuments(searchTerm, createFieldsToSearchOver(), filters, SolrOperator.AND);
	}
	
	/***
	 * Searches documents for most popular within all networks and all private user data
	 * 
	 * @param searchTerm
	 * @param userIdFilter
	 * @param socialNetwork
	 * @return
	 */
	public List<Document> searchIndexedDocumentsWithinAllProviders(String searchTerm, Long userIdFilter) {

		// filters
		Map<String, Object> filters = new HashMap<String, Object>();

		List<Long> values = new LinkedList<Long>();
		// private user data has owner id within all providers
		Long ownerId = SearchKeys.generateOwnerId(userIdFilter);
		values.add(ownerId);
		
		// most popular data has no owner within all providers
		Long noOwner = SearchKeys.generateOwnerId(null);
		values.add(noOwner);
		filters.put(SearchKeys.Fields.FIELD_OWNER_ID, values);
		Integer resultsLimit = networkNumbers * networkLimit;
		return searchEngine.searchDocuments(searchTerm, createFieldsToSearchOver(), filters, SolrOperator.OR, SearchKeys.Fields.FIELD_EXTERNAL_NETWORK_ID, networkLimit, resultsLimit);
	}
	
	
	

	/***
	 * Searches documents by a search term and user id across all social networks and content providers
	 * 
	 * @param searchTerm
	 * @param userIdFilter
	 * @return
	 */
	public List<Document> searchIndexedDocuments(String searchTerm, Long userIdFilter) {

		// filters
		Map<String, Object> filters = new HashMap<String, Object>();
		
		Long ownerId = SearchKeys.generateOwnerId(userIdFilter);
		filters.put(SearchKeys.Fields.FIELD_OWNER_ID, ownerId);


		return searchEngine.searchDocuments(searchTerm, createFieldsToSearchOver(), filters, SolrOperator.AND);
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
				SearchKeys.Fields.FIELD_CONTACT_DISPLAY_NAME,
				SearchKeys.Fields.FIELD_CATEGORY
		};
	}


}
