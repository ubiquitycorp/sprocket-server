package com.ubiquity.sprocket.repository;

import org.apache.commons.lang3.StringUtils;
import org.lilyproject.repository.api.Record;
import org.lilyproject.repository.api.Repository;

import com.niobium.repository.BaseRepositoryLilyImpl;
import com.ubiquity.identity.domain.User;
import com.ubiquity.integration.domain.Activity;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.VideoContent;
import com.ubiquity.location.domain.Place;
import com.ubiquity.sprocket.domain.Content;

public class ContentRepositoryLilyImpl extends BaseRepositoryLilyImpl <Content> implements ContentRepository {

	public ContentRepositoryLilyImpl(String namespace, Repository repository) {
		super(namespace, repository, Content.class);
	}

	@Override
	public void create(Content content) {
		Record record = prepareNewRecord();
		loadContentRecord(record, content);
				
		record = create(record);
		content.setContentId(record.getId().getMaster().toString());
		
	}

	@Override
	public Content read(String id) {
		Record record = readExistingRecord(id);
		return assembleContentRecord(id, record);
	}



	@Override
	public void update(Content content) {
//		try {
//			Record record = prepareNewRecord(profile.getProfileId());
//			setFieldValue(record, "gender", profile.getGender().ordinal());
//			AgeRange ageRange = profile.getAgeRange();
//			if(ageRange != null) {
//				setFieldValue(record, "min_age", ageRange.getMin());
//				setFieldValue(record, "max_age", ageRange.getMax());
//			}
//			setFieldValue(record, "search_history", profile.getSearchHistory());
//			update(record);
//		} catch (RecordException e) {
//			throw new RuntimeException("Could not update record", e);
//		}
	}

	@Override
	public void delete(Content content) {
		//throw new UnsupportedOperationException();
	}

	@Override
	public Content updateAndSelect(Content content) {
		throw new UnsupportedOperationException();
	}


	private Content assembleContentRecord(String id, Record record) {
		Content.Builder contentBuilder = new Content.Builder();
		contentBuilder.contentId(id);
		
		// determine type and set activity, place, or video accordingly
		String type = (String)getFieldValue(record, "type");
		if(type.equals(Activity.class.getSimpleName())) {
			contentBuilder.activity(assembleActivity(record));
		}
		return contentBuilder.build();
		
	}
	
	private Activity assembleActivity(Record record) {
		Activity.Builder activityBuilder = new Activity.Builder();
		activityBuilder.owner(new User((Long)getFieldValue(record, "user_id")));
		activityBuilder.externalIdentifier((String)getFieldValue(record, "external_identifier"));
		activityBuilder.externalNetwork(ExternalNetwork.getNetworkById((Integer)getFieldValue(record, "external_network")));
		activityBuilder.title((String)getFieldValue(record, "title"));
		activityBuilder.body((String)getFieldValue(record, "body"));

		return activityBuilder.build();
	}
	
	private void loadContentRecord(Record record, Content content) {
		
		if(content.getVideo() != null) {
			setFieldValue(record, "type", VideoContent.class.getSimpleName());
			loadVideoRecord(record, content.getVideo());
			return;
		} if(content.getActivity() != null) {
			setFieldValue(record, "type", Activity.class.getSimpleName());
			loadActivityRecord(record, content.getActivity());
			return;
		} if(content.getPlace() != null) {
			setFieldValue(record, "type", Place.class.getSimpleName());
			loadPlaceRecord(record, content.getPlace());
			return;
		}
		throw new IllegalArgumentException("Cannot determine content type from content");
	}
	
	
	private void loadVideoRecord(Record record, VideoContent video) {
		if(video.getOwner() != null)
			setFieldValue(record, "user_id", video.getOwner().getUserId());
		setFieldValue(record, "external_identifier", video.getVideo().getItemKey());
		setFieldValue(record, "external_network", video.getExternalNetwork().ordinal());
		setFieldValue(record, "title", video.getTitle());
		setFieldValue(record, "body", video.getDescription());
	}
	
	private void loadActivityRecord(Record record, Activity activity) {
		if(activity.getOwner() != null)
			setFieldValue(record, "user_id", activity.getOwner().getUserId());
		setFieldValue(record, "external_identifier", activity.getExternalIdentifier());
		setFieldValue(record, "external_network", activity.getExternalNetwork().ordinal());
		setFieldValue(record, "title", activity.getTitle());
		setFieldValue(record, "body", activity.getBody());
	}
	
	private void loadPlaceRecord(Record record, Place place) {
		setFieldValue(record, "external_identifier", place.getExternalIdentifier());
		setFieldValue(record, "external_network", place.getExternalNetwork().ordinal());
		setFieldValue(record, "title", place.getName());
		if(place.getDescription() != null)
			setFieldValue(record, "body", StringUtils.stripToEmpty(place.getDescription()));
		
	}
	
	
}
