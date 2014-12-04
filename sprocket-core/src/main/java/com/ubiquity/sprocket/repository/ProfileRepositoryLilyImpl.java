package com.ubiquity.sprocket.repository;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.lilyproject.repository.api.FieldNotFoundException;
import org.lilyproject.repository.api.Record;
import org.lilyproject.repository.api.RecordScan;
import org.lilyproject.repository.api.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.lily.BaseRepositoryLilyImpl;
import com.niobium.repository.lily.LilyRepositoryFactory;
import com.niobium.repository.mr.MapReduceOutputFile;
import com.ubiquity.integration.domain.AgeRange;
import com.ubiquity.integration.domain.Gender;
import com.ubiquity.sprocket.domain.Profile;
import com.ubiquity.sprocket.repository.mapreduce.SearchTermMapper;
import com.ubiquity.sprocket.repository.mapreduce.SearchTermReducer;

public class ProfileRepositoryLilyImpl extends BaseRepositoryLilyImpl <Profile> implements ProfileRepository {

	private Logger log = LoggerFactory.getLogger(getClass());
	private String jobOutputDir;

	/***
	 * Creates a repo with the name space and repository
	 * 
	 * @param namespace
	 * @param repository
	 */
	public ProfileRepositoryLilyImpl(String namespace, Repository repository) {
		super(namespace, repository, Profile.class);
	}


	/***
	 * Creates a repo with a job output directory
	 * 
	 * @param namespace
	 * @param repository
	 * @param jobOutputDir
	 */
	public ProfileRepositoryLilyImpl(String namespace, Repository repository, String jobOutputDir) {
		this(namespace, repository);
		this.jobOutputDir = jobOutputDir;
	}



	@Override
	public void create(Profile profile) {
		Record record = prepareNewRecord(profile.getUserId().toString());
		loadProfileRecord(record, profile);
		loadIdentities(record, profile);

		record = create(record);
		// set the master id
		profile.setProfileId(record.getId().getMaster().toString());

	}

	private String generateIdentityId(Profile profile) {
		return profile.getUserId().toString() + "-" + String.valueOf(profile.getExternalNetwork()) + "-" + profile.getExternalIdentifier();
	}

	private void loadIdentities(Record record, Profile profile) {
		// now create contact records, if they exist
		List<Profile> identities = profile.getIdentities();

		if(!identities.isEmpty()) {
			List<Record> records = new LinkedList<Record>();
			for(Profile identity : identities) {
				Record identityRecord = prepareNewRecord(generateIdentityId(profile), "identity");
				loadProfileRecord(identityRecord, identity);
				records.add(identityRecord);
			}
			log.info("loaded identities {}", records);

			setFieldValue(record, "identities", records);
		}
	}

	private void loadProfileRecord(Record record, Profile profile) {

		Gender gender = profile.getGender();
		if(profile.getGender() != null)
			setFieldValue(record, "gender", gender.ordinal());

		AgeRange ageRange = profile.getAgeRange();
		if(ageRange != null) {
			setFieldValue(record, "min_age", ageRange.getMin());
			setFieldValue(record, "max_age", ageRange.getMax());
		}
		setFieldValue(record, "search_history", profile.getSearchHistory());
	}

	@Override
	public Profile read(String id) {
		Record record = readExistingRecord(id);
		return assemble(id, record);
	}

	@SuppressWarnings("unchecked")
	public Profile assemble(String id, Record record) {
		Profile.Builder profileBuilder = new Profile.Builder()
		.profileId(id);
		if(id != null) // lily bug is not returning the record
			profileBuilder.userId(Long.valueOf(getIdWithStrippedPrefix(id)));

		profileBuilder.gender(Gender.getGenderById((Integer)getFieldValue(record, "gender")));

		// look for min or max age; if either one is set, then we'll store an age range value
		try {
			Integer minAge = (Integer)getFieldValue(record, "min_age");
			Integer maxAge = (Integer)getFieldValue(record, "max_age");
			if(minAge != null || maxAge != null) {
				profileBuilder.ageRange(new AgeRange(minAge, maxAge));
			}
		} catch (FieldNotFoundException e) {}


		Profile profile = profileBuilder.build();
		// now add collections
		profile.getSearchHistory().addAll((List<String>)getFieldValue(record, "search_history"));

		List<Record> identityRecords = (List<Record>)getFieldValue(record, "identities");
		if(identityRecords != null && !identityRecords.isEmpty()) {
			if(!identityRecords.isEmpty()) {
				for(Record identityRecord : identityRecords) {
					Profile identity = assemble(null, identityRecord);
					profile.getIdentities().add(identity);
				}
			}
		}
		return profile;
	}



	@Override
	public void update(Profile profile) {
		Record record = readExistingRecord(profile.getProfileId());
		loadProfileRecord(record, profile);
		loadIdentities(record, profile);

		update(record);
	}

	@Override
	public void delete(Profile profile) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Profile updateAndSelect(Profile obj) {
		throw new UnsupportedOperationException();
	}


	@Override
	public MapReduceOutputFile getMostPopularSearchTerms() {
		if(jobOutputDir == null)
			throw new IllegalArgumentException("Job output dir is not set");

		// construct the path
		String jobOutputPath = new StringBuilder(jobOutputDir)
		.append("/")
		.append("most-popular-search")
		.append("/")
		.append(System.currentTimeMillis())
		.toString();
		log.info("Setting job output path to {}", jobOutputPath);
		try {
			
			Configuration configuration = new Configuration();
			int res = ToolRunner.run(configuration, new MostPopularSearchTermJob(jobOutputPath), null);
			if(res == 0) {
				log.info("Job completed");
				FileSystem fs = FileSystem.get(configuration);
				FSDataInputStream is = fs.open(new Path(jobOutputPath +"/part-r-00000"));
				MapReduceOutputFile output = new MapReduceOutputFile.Builder().itemKey("most-popular-search.csv").build();
				output.setInputStream(is);
				return output;
			} else {
				throw new RuntimeException("Job was not successful");
			}

		} catch (Exception e) {
			throw new RuntimeException("Unable to run map reduce job", e);
		}
	}

	private class MostPopularSearchTermJob extends Configured implements Tool {

		private String jobOutputPath;
		
		public MostPopularSearchTermJob(String jobOutputPath) {
			this.jobOutputPath = jobOutputPath;
		}
		@Override
		public int run(String[] args) throws Exception {
			Configuration config = getConf();

			config.set("mapred.textoutputformat.separator", ","); //Prior to Hadoop 2 (YARN)
			config.set("mapreduce.output.textoutputformat.separator", ",");
			config.set("mapreduce.output.key.field.separator", ",");
			config.set("mapred.textoutputformat.separatorText", ","); // ?

			Job job = new Job(config, "MostPopularSearchTerm");
			job.setJarByClass(this.getClass());

			job.setMapperClass(SearchTermMapper.class);
			job.setReducerClass(SearchTermReducer.class);
			job.setNumReduceTasks(1);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(IntWritable.class);

			job.setOutputFormatClass(TextOutputFormat.class);

//			String jobOutputPath = new StringBuilder(jobOutputDir)
//			.append("/")
//			.append(job.getJobName().toLowerCase())
//			.append("/")
//			.append(System.currentTimeMillis())
//			.toString();
//			log.info("Setting job output path to {}", jobOutputPath);

			TextOutputFormat.setOutputPath(job, new Path(jobOutputPath));

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

	public static void main(String[] args)throws Exception {

		PropertiesConfiguration config = new PropertiesConfiguration("test.properties");
		config.setProperty("lily.client.session.timeout", 10);
		config.setProperty("lily.client.connection", "vm-data:2181");
		config.setProperty("lily.repository.name", "test");
		config.setProperty("hbase.sprocket.namespace", "sprocket.schema");
		config.setProperty("mapreduce.job.output.dir", "mapreduce.job.output.dir");

		String namespace = "sprocket.schema";
		String jobOutputDir = "/tmp/hadoop";
		LilyRepositoryFactory.initialize(config);


		ProfileRepository profileRepository = new ProfileRepositoryLilyImpl(namespace, 
				LilyRepositoryFactory.createRepository(), jobOutputDir);
		MapReduceOutputFile output = profileRepository.getMostPopularSearchTerms();

	}


}
