package com.ubiquity.sprocket.repository;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.identity.domain.User;
import com.ubiquity.integration.domain.Activity;
import com.ubiquity.integration.domain.VideoContent;
import com.ubiquity.location.domain.Place;
import com.ubiquity.media.domain.Video;
import com.ubiquity.sprocket.domain.Content;
import com.ubiquity.sprocket.domain.UserEngagement;
import com.ubiquity.sprocket.repository.hbase.ContentPK;

public class ContentRepositoryHBaseImpl extends BaseRepositoryHBaseImpl <Content> implements ContentRepository {

	@SuppressWarnings("unused")
	private Logger log = LoggerFactory.getLogger(getClass());

	public ContentRepositoryHBaseImpl() {
		super(Content.class);
	}

	@Override
	public void create(Content content) {
		try {
			Put put = new Put(Bytes.toBytes(content.getContentId().toString()));
			loadContentIntoPut(put, content);
			put(put);
		} finally {
			close();
		}
	}

	@Override
	public Content read(ContentPK key) {

		try {
			Get get = new Get(Bytes.toBytes(key.toString()));
			get.addFamily(Bytes.toBytes(HBaseSchema.ColumnFamilies.ATTRIBUTES));
			return assembleContentFromGet(key, get);

		} finally {
			close();
		}
	}

	@Override
	public void update(Content content) {
		create(content);
	}

	@Override
	public void delete(Content content) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Content updateAndSelect(Content content) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addUserEngagement(UserEngagement engagement) {
		String qualifier = HBaseSchema.Qualifiers.PREFIX_ENGAGED + "-" + engagement.getGroupMembership() + "-" +
				engagement.getUserId().toString();
		try {
			incrementValue(engagement.getContentId().toString(), HBaseSchema.ColumnFamilies.HISTORY, qualifier, 1l);
		} finally {
			close();
		}
	}

	@Override
	public Long getEngagementCount(ContentPK contentId) {

		Long count = new Long(0);

		Scan scan = createScanWithPrefixFilter(contentId.toString(), HBaseSchema.ColumnFamilies.HISTORY, HBaseSchema.Qualifiers.PREFIX_ENGAGED);
		try {
			ResultScanner rs = getTable().getScanner(scan);
			for (Result r = rs.next(); r != null; r = rs.next()) {
				for (Cell cell : r.rawCells()) {
					count += new BigInteger(cell.getValueArray()).longValue();
				}
			}
			rs.close();

		} catch (IOException e) {
			throw new RuntimeException("Could not scan row", e);
		}
		return count;
	}

	@Override
	public Long getEngagementCount(ContentPK contentId, String groupMembership) {
		Long count = new Long(0);

		Scan scan = createScanWithPrefixFilter(contentId.toString(), 
				HBaseSchema.ColumnFamilies.HISTORY, HBaseSchema.Qualifiers.PREFIX_ENGAGED + "-" + groupMembership);
		try {
			ResultScanner rs = getTable().getScanner(scan);
			for (Result r = rs.next(); r != null; r = rs.next()) {
				for (Cell cell : r.rawCells()) {
					count += new BigInteger(cell.getValueArray()).longValue();
				}
			}
			rs.close();

		} catch (IOException e) {
			throw new RuntimeException("Could not scan row", e);
		}
		return count;
	}

	@Override
	public List<Content> findMostEngagedByGroup(String groupMembership, int n) {
		
		
		Content[] mostEngaged = new Content[n];
		Long[] highestValues = new Long[n];
		Arrays.fill(highestValues, 0l);

		Scan scan = createScanWithPrefixFilter(HBaseSchema.ColumnFamilies.HISTORY, HBaseSchema.Qualifiers.PREFIX_ENGAGED + "-" + groupMembership);
		try {
			ResultScanner rs = getTable().getScanner(scan);
			for (Result r = rs.next(); r != null; r = rs.next()) {
				// each row
				Long count = new Long(0);
				for (Cell cell : r.rawCells()) {
					count += new BigInteger(cell.getValueArray()).longValue(); // count the number of times something was hig
					for(int i = 0; i < highestValues.length; i++) {
						if(count > highestValues[i]) {
							highestValues[i] = count;
							mostEngaged[i] = read(ContentPK.fromString(new String(r.getRow())));
							break;
						}
					}
				}
			}
			rs.close();

		} catch (IOException e) {
			throw new RuntimeException("Could not scan row", e);
		}
		
		List<Content> results = new LinkedList<Content>();
		for(int i = 0; i < mostEngaged.length;i++) {
			Content content = mostEngaged[i];
			if(content != null)
				results.add(content);
		}
		
		return results;
	}






	/**
	 *
	 * Private writer methods
	 *
	 */

	private void loadContentIntoPut(Put put, Content content) {
		if(content.getVideoContent() != null) {
			addValue(put, HBaseSchema.ColumnFamilies.ATTRIBUTES, HBaseSchema.Qualifiers.TYPE, VideoContent.class.getSimpleName());
			loadVideoIntoPut(put, content.getVideoContent());

		} else if(content.getActivity() != null) {
			addValue(put, HBaseSchema.ColumnFamilies.ATTRIBUTES, HBaseSchema.Qualifiers.TYPE, Activity.class.getSimpleName());
			loadActivityIntoPut(put, content.getActivity());

		} else if(content.getPlace() != null) {
			addValue(put, HBaseSchema.ColumnFamilies.ATTRIBUTES, HBaseSchema.Qualifiers.TYPE, Place.class.getSimpleName());
			loadPlaceIntoPut(put, content.getPlace());

		} else {
			throw new IllegalArgumentException("Cannot determine content type from content");
		}

	}


	private void loadPlaceIntoPut(Put put, Place business) {
		addValue(put, HBaseSchema.ColumnFamilies.ATTRIBUTES, HBaseSchema.Qualifiers.NAME, business.getName());
		if(business.getDescription() != null)
			addValue(put, HBaseSchema.ColumnFamilies.ATTRIBUTES, HBaseSchema.Qualifiers.DESCRIPTION, business.getDescription());
	}

	private void loadActivityIntoPut(Put put, Activity activity) {
		if(activity.getOwner() != null)
			addValue(put, HBaseSchema.ColumnFamilies.ATTRIBUTES, HBaseSchema.Qualifiers.OWNER_ID, activity.getOwner().getUserId());

		addValue(put, HBaseSchema.ColumnFamilies.ATTRIBUTES, HBaseSchema.Qualifiers.NAME, activity.getTitle());
		addValue(put, HBaseSchema.ColumnFamilies.ATTRIBUTES, HBaseSchema.Qualifiers.DESCRIPTION, activity.getBody());

	}

	private void loadVideoIntoPut(Put put, VideoContent videoContent) {
		if(videoContent.getOwner() != null)
			addValue(put, HBaseSchema.ColumnFamilies.ATTRIBUTES, HBaseSchema.Qualifiers.OWNER_ID, videoContent.getOwner().getUserId());

		addValue(put, HBaseSchema.ColumnFamilies.ATTRIBUTES, HBaseSchema.Qualifiers.NAME, videoContent.getTitle());
		addValue(put, HBaseSchema.ColumnFamilies.ATTRIBUTES, HBaseSchema.Qualifiers.DESCRIPTION, videoContent.getDescription());
	}


	/**
	 * 
	 * Private reader methods
	 * 
	 */

	private Content assembleContentFromResult(ContentPK rowKey, Result result) {
		
		Content.Builder contentBuilder = new Content.Builder();
		contentBuilder.contentId(rowKey);
		
		// determine type and set activity, place, or video accordingly
		String type = getStringValue(result, HBaseSchema.ColumnFamilies.ATTRIBUTES, HBaseSchema.Qualifiers.TYPE);
		if(type.equals(Activity.class.getSimpleName())) {
			contentBuilder.activity(assembleActivityFromResult(rowKey, result));
		} else if(type.equals(VideoContent.class.getSimpleName())) {
			contentBuilder.videoContent(assembleVideoContentFromResult(rowKey, result));
		} else if(type.equals(Place.class.getSimpleName())) {
			contentBuilder.place(assemblePlaceFromResult(rowKey, result));
		} else {
			throw new IllegalArgumentException("Unknown content type: " + type);
		}
		Content content = contentBuilder.build();
		return content;
	}
	
	private Content assembleContentFromGet(ContentPK rowKey, Get get) {
		// execute query and get result back
		Result result = getResult(get);
		
		return assembleContentFromResult(rowKey, result);
	}

	private Activity assembleActivityFromResult(ContentPK rowKey, Result result) {

		Activity.Builder activityBuilder = new Activity.Builder();

		// determine type and set activity, place, or video accordingly
		Long ownerId = getLongValue(result, HBaseSchema.ColumnFamilies.ATTRIBUTES, HBaseSchema.Qualifiers.OWNER_ID);
		if(ownerId != null)		
			activityBuilder.owner(new User(ownerId));

		activityBuilder.externalIdentifier(rowKey.getIdentifier())
		.externalNetwork(rowKey.getExternalNetwork())
		.title(getStringValue(result, HBaseSchema.ColumnFamilies.ATTRIBUTES, HBaseSchema.Qualifiers.NAME))
		.body(getStringValue(result, HBaseSchema.ColumnFamilies.ATTRIBUTES, HBaseSchema.Qualifiers.DESCRIPTION));

		return activityBuilder.build();
	}

	private VideoContent assembleVideoContentFromResult(ContentPK rowKey, Result result) {
		VideoContent.Builder videoBuilder = new VideoContent.Builder();

		// determine type and set activity, place, or video accordingly
		Long ownerId = getLongValue(result, HBaseSchema.ColumnFamilies.ATTRIBUTES, HBaseSchema.Qualifiers.OWNER_ID);
		if(ownerId != null)	
			videoBuilder.owner(new User(ownerId));

		videoBuilder.video(new Video.Builder().itemKey(rowKey.getIdentifier()).build())
		.externalNetwork(rowKey.getExternalNetwork())
		.title(getStringValue(result, HBaseSchema.ColumnFamilies.ATTRIBUTES, HBaseSchema.Qualifiers.NAME))
		.description(getStringValue(result, HBaseSchema.ColumnFamilies.ATTRIBUTES, HBaseSchema.Qualifiers.DESCRIPTION));

		return videoBuilder.build();
	}

	private Place assemblePlaceFromResult(ContentPK rowKey, Result result) {
		Place.Builder placeBuilder = new Place.Builder();
		placeBuilder.externalIdentifier(rowKey.getIdentifier())
		.externalNetwork(rowKey.getExternalNetwork())
		.name(getStringValue(result, HBaseSchema.ColumnFamilies.ATTRIBUTES, HBaseSchema.Qualifiers.NAME))
		.description(getStringValue(result, HBaseSchema.ColumnFamilies.ATTRIBUTES, HBaseSchema.Qualifiers.DESCRIPTION));
		return placeBuilder.build();
	}








}
