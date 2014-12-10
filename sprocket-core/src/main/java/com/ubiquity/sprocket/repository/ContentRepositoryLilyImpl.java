package com.ubiquity.sprocket.repository;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.lilyproject.repository.api.Record;
import org.lilyproject.repository.api.Repository;

import com.niobium.repository.lily.BaseRepositoryLilyImpl;
import com.ubiquity.identity.domain.User;
import com.ubiquity.integration.domain.Activity;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.VideoContent;
import com.ubiquity.location.domain.Place;
import com.ubiquity.media.domain.Video;
import com.ubiquity.sprocket.domain.Content;
import com.ubiquity.sprocket.domain.UserEngagement;

public class ContentRepositoryLilyImpl extends BaseRepositoryLilyImpl <Content> implements ContentRepository {

	public ContentRepositoryLilyImpl(String namespace, Repository repository) {
		super(namespace, repository, Content.class);
	}

	@Override
	public void create(Content content) {
		Record record = prepareNewRecord(generateContentId(content));
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
		Record record = prepareExistingRecord(content.getContentId());
		loadContentRecord(record, content);
		update(record);
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
		} else if(type.equals(VideoContent.class.getSimpleName())) {
			contentBuilder.videoContent(assembleVideoContent(record));
		} else if(type.equals(Place.class.getSimpleName())) {
			contentBuilder.place(assemblePlace(record));
		} else {
			throw new IllegalArgumentException("Unknown content type: " + type);
		}
		Content content = contentBuilder.build();

		// load engagement
		@SuppressWarnings("unchecked")
		List<Record> engagementRecords = (List<Record>)getFieldValue(record, "user_engagement_history");		
		if(engagementRecords != null) {
			for(Record engagementRecord : engagementRecords) {
				content.getUserEngagement().add(assembleUserEngagement(engagementRecord));
			}
		}
		return content;

	}


	private UserEngagement assembleUserEngagement(Record record) {
		return new UserEngagement(new User((Long)getFieldValue(record, "user_id")),
				(Long)getFieldValue(record, "client_timestamp"));
	}

	private Place assemblePlace(Record record) {
		Place.Builder placeBuilder = new Place.Builder();
		placeBuilder.externalIdentifier((String)getFieldValue(record, "external_identifier"))
		.externalNetwork(ExternalNetwork.getNetworkById((Integer)getFieldValue(record, "external_network")))
		.name((String)getFieldValue(record, "title"))
		.description((String)getFieldValue(record, "body"));

		return placeBuilder.build();
	}
	private Activity assembleActivity(Record record) {
		Activity.Builder activityBuilder = new Activity.Builder();
		activityBuilder.owner(new User((Long)getFieldValue(record, "user_id")))
		.externalIdentifier((String)getFieldValue(record, "external_identifier"))
		.externalNetwork(ExternalNetwork.getNetworkById((Integer)getFieldValue(record, "external_network")))
		.title((String)getFieldValue(record, "title"))
		.body((String)getFieldValue(record, "body"));

		return activityBuilder.build();
	}

	private VideoContent assembleVideoContent(Record record) {
		VideoContent.Builder videoBuilder = new VideoContent.Builder();
		videoBuilder.owner(new User((Long)getFieldValue(record, "user_id")))
		.video(new Video.Builder().itemKey((String)getFieldValue(record, "external_identifier")).build())
		.externalNetwork(ExternalNetwork.getNetworkById((Integer)getFieldValue(record, "external_network")))
		.title((String)getFieldValue(record, "title"))
		.description((String)getFieldValue(record, "body"));

		return videoBuilder.build();
	}

	private void loadContentRecord(Record record, Content content) {

		if(content.getVideoContent() != null) {
			setFieldValue(record, "type", VideoContent.class.getSimpleName());
			loadVideoRecord(record, content.getVideoContent());
			
		} else if(content.getActivity() != null) {
			setFieldValue(record, "type", Activity.class.getSimpleName());
			loadActivityRecord(record, content.getActivity());
		
		} else if(content.getPlace() != null) {
			setFieldValue(record, "type", Place.class.getSimpleName());
			loadPlaceRecord(record, content.getPlace());
		} else {
			throw new IllegalArgumentException("Cannot determine content type from content");
		}
		
		List<UserEngagement> engagementHistory = content.getUserEngagement();
		if(!engagementHistory.isEmpty()) {
			List<Record> records = new LinkedList<Record>();
			for(UserEngagement engagement : engagementHistory) {
				Record engagementRecord = prepareNewRecord(generateContentId(content), "user_engagement");
				setFieldValue(engagementRecord, "user_id", engagement.getUser().getUserId());
				setFieldValue(engagementRecord, "client_timestamp", engagement.getTimestamp());
				records.add(engagementRecord);
			}
			setFieldValue(record, "user_engagement_history", records);
		}
		
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
		setFieldValue(record, "body", StringUtils.stripToEmpty(place.getDescription()));

	}

	public static String generateContentId(Content content) {
			if(content.getActivity() != null) {
				Activity activity = content.getActivity();
				return String.valueOf(activity.getExternalNetwork().ordinal()) + activity.getExternalIdentifier();
			} else if(content.getVideoContent() != null) {
				VideoContent videoContent = content.getVideoContent();
				return String.valueOf(videoContent.getExternalNetwork().ordinal()) + videoContent.getVideo().getItemKey();
			} else if(content.getPlace() != null) {
				Place place = content.getPlace();
				return String.valueOf(place.getPlaceId());
			}
			throw new IllegalArgumentException("Un supported content type");
			
		
	}
}
