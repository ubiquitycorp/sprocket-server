package com.ubiquity.sprocket.search;


public class SearchKeys {

	private static final String DELIMITER = ":";

	public static class Fields {
		public static final String FIELD_ID = "id";
		public static final String FIELD_EXTERNAL_NETWORK_ID = "external_network_id";
		public static final String FIELD_EXTERNAL_IDENTIFIER = "external_identifier";

		public static final String FIELD_TITLE = "title";
		public static final String FIELD_DESCRIPTION = "description";
		public static final String FIELD_DATA_TYPE = "data_type";
		public static final String FIELD_BODY = "body";
		public static final String FIELD_THUMBNAIL = "thumbnail";
		public static final String FIELD_CATEGORY = "category";
		public static final String FIELD_ITEM_KEY = "item_key";
		public static final String FIELD_CONTACT_DISPLAY_NAME = "contact_display_name";
		public static final String FIELD_CONTACT_IDENTIFIER = "contact_identifier";
		public static final String FIELD_CONTACT_THUMBNAIL = "contact_thumbnail";

		public static final String FIELD_URL = "url";
		public static final String FIELD_ACTIVITY_TYPE = "activity_type";
		public static final String FIELD_OWNER_ID = "owner_id";

	}
	
	public static class Values {
		public static final Long EMPTY_OWNER_ID = 0l;
	}
	
	/**
	 * Will return a value of the owner id that is: (or the empty default) : passed in id; for example "-1:334333:Video"
	 * @param userFilterId
	 * @param id
	 * 
	 * @return
	 */
	public static String generateDocumentKeyForId(Long userFilterId, Long id, String dataType) {
		Long ownerId = generateOwnerId(userFilterId);
		return new StringBuilder().append(ownerId).append(DELIMITER).append(id).append(DELIMITER).append(dataType).toString();
	}	
	
	/***
	 * Returns default empty vaue if the passed in value is null, else the passed in reference is returned
	 * 
	 * @param userIdFilter
	 * @return
	 */
	public static Long generateOwnerId(Long userIdFilter) {
		return userIdFilter == null ? SearchKeys.Values.EMPTY_OWNER_ID : userIdFilter;
	}
	
	
}
