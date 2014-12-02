package com.ubiquity.sprocket.repository;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.lilyproject.mapreduce.RecordIdWritable;
import org.lilyproject.mapreduce.RecordMapper;
import org.lilyproject.mapreduce.RecordWritable;
import org.lilyproject.repository.api.FieldNotFoundException;
import org.lilyproject.repository.api.Record;
import org.lilyproject.repository.api.RecordException;
import org.lilyproject.repository.api.RecordScan;
import org.lilyproject.repository.api.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.lily.BaseRepositoryLilyImpl;
import com.niobium.repository.lily.LilyRepositoryFactory;
import com.ubiquity.identity.domain.User;
import com.ubiquity.integration.domain.Activity;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.VideoContent;
import com.ubiquity.location.domain.Place;
import com.ubiquity.media.domain.Video;
import com.ubiquity.sprocket.domain.Content;
import com.ubiquity.sprocket.domain.UserEngagement;

public class ContentRepositoryLilyImpl extends BaseRepositoryLilyImpl <Content> implements ContentRepository {

	private Logger log = LoggerFactory.getLogger(getClass());

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

		Record record = readExistingRecord(content.getContentId());
		loadContentRecord(record, content);
		update(record);
	}

	@Override
	public void delete(Content content) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Content updateAndSelect(Content content) {
		throw new UnsupportedOperationException();
	}
	
	protected int countAllUserEngagmentForContentType(String type) {
		return 0;
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
		return new UserEngagement.Builder().userId((Long)getFieldValue(record, "user_id"))
				.timestamp((Long)getFieldValue(record, "client_timestamp"))
				.groupMembership((String)getFieldValue(record, "group_membership")).build();
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
				setFieldValue(engagementRecord, "user_id", engagement.getUserId());
				setFieldValue(engagementRecord, "client_timestamp", engagement.getTimestamp());
				setFieldValue(engagementRecord, "group_membership", engagement.getGroupMembership());
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

	private class MedianEngagedContentJob extends Configured implements Tool {

		@Override
		public int run(String[] args) throws Exception {
			Configuration config = getConf();

			Job job = new Job(config, "MedianEngagedContentJob");
			job.setJarByClass(this.getClass());

			job.setMapperClass(MedianEngagedContentMapper.class);
			job.setReducerClass(MedianEngagedContentReducer.class);
			job.setNumReduceTasks(1);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(IntWritable.class);

			// The reducer writes directly to Lily, so for Hadoop there is no output to produce
			job.setOutputFormatClass(NullOutputFormat.class);

			// serialize the record scan
			RecordScan findAll = createRecordScan();
			initJob(findAll,  LilyRepositoryFactory.getZookeeperConnectionString(), job);

			// Launch the job
			boolean b = job.waitForCompletion(true);
			if (!b) {
				throw new IOException("error executing job!");
			}

			return 0;
		}
	}

	
	


	public static class MedianEngagedContentMapper extends RecordMapper<Text, IntWritable> {


		private Logger log = LoggerFactory.getLogger(getClass());

		private Text keyOut = new Text();

		@SuppressWarnings("unchecked")
		@Override
		protected void map(RecordIdWritable recordIdWritable, RecordWritable recordWritable, Context context)
				throws IOException, InterruptedException {


			log.info("mapping {}", recordIdWritable.getRecordId());
			// get the search history for this profile
			Record record = recordWritable.getRecord();

			try {
				List<Record> engagementRecords = (List<Record>)record.getField("user_engagement_history");				
				keyOut.set(new String(recordIdWritable.getRecordId().toString()));
				context.write(keyOut, new IntWritable(engagementRecords.size()));
			} catch (FieldNotFoundException e) {
				log.info("No engagement records for this content");
			} catch (RecordException e) {
				throw new RuntimeException("Could not read engagement records", e);
			}
		}
	}
	
	public static class MedianEngagedContentReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
		
		private Logger log = LoggerFactory.getLogger(getClass());
		
		int totalForType;
		
		@Override
		protected void setup(Context context) throws IOException, InterruptedException {
			super.setup(context);
			
			
		}
		
		@Override
	    protected void cleanup(Context context) throws IOException, InterruptedException {
	        super.cleanup(context);
	    }

	    @Override
	    protected void reduce(Text key, Iterable<IntWritable> values, Context context)
	            throws IOException, InterruptedException {

	        int sum = 0;
	        for (IntWritable val : values) {
		    	log.info("reducing key: {} value: {}", key, val);
	            sum += val.get();
	        }
	        
	        context.write(key, new IntWritable(sum));

	       
	    }

	}



	@Override
	public void updateMedianEngagedContent() {
		try {
			Configuration configuration = new Configuration();
			int res = ToolRunner.run(configuration, new MedianEngagedContentJob(), null);
			if(res == 0) {
				log.info("Job completed");
			} else {
				throw new RuntimeException("Job was not successful");
			}
		} catch (Exception e) {
			throw new RuntimeException("Job was not successful", e);
		}
	}
	
	public static void main(String[] args) {
		int[] input = { 3, 8, 9, 3, 2, 7, 10, 34, 28, 1, 5, 2, 19 };
		List<Integer> aggregated = new LinkedList<Integer>();
		int atMedian = -1;
		double currentMedian;
		double leastDistanceFromMedian = Double.MAX_VALUE;
		
		// Try sorting the array first. Then after it's sorted, if the array has an even amount of elements the mean of the middle two is the median, if it has a odd number, the middle element is the median.
		for(int i = 0; i < input.length; i++) {
			int value = input[i];
			aggregated.add(value);
			Collections.sort(aggregated);
			currentMedian = median(aggregated);
			double d = Math.abs(currentMedian - value);
			if(d < leastDistanceFromMedian) {

				System.out.println(" " + value + " is closest to current median" + currentMedian);
			}
		}
		
		//System.out.println("sorted list"+ aggregated);


	}
	
	public static double median(List<Integer> aggregated) {
		double median;
		int middle = aggregated.size() / 2;
	    if (aggregated.size() % 2 == 1) {
	        median = aggregated.get(middle);
	    } else {
	        median = (aggregated.get(middle - 1) + aggregated.get(middle)) / 2.0;
	    }
	    return median;
	}

}
