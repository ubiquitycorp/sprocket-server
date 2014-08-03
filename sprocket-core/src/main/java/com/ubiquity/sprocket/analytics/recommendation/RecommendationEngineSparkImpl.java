package com.ubiquity.sprocket.analytics.recommendation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.clustering.KMeans;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.content.domain.VideoContent;
import com.ubiquity.identity.domain.User;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.domain.Contact;

public class RecommendationEngineSparkImpl implements RecommendationEngine {

	private Logger log = LoggerFactory.getLogger(getClass());

	private int kMeansMaxIterations;
	private int kMeansRuns;
	private int kMeansK;

	private KMeansModel model;

	private JavaRDD<Contact> distData;
	private JavaSparkContext sparkContext;
	private List<Contact> dataStore = new LinkedList<Contact>();


	/**
	 * Represents the instance space
	 */
	private JavaRDD<Vector> points;

	private List<Dimension> dimensions = new ArrayList<Dimension>();



	public RecommendationEngineSparkImpl(Configuration configuration) {
		sparkContext = new JavaSparkContext(configuration.getString("recommendation.engine.hadoop.master"), configuration.getString("recommendation.engine.appname"));
		kMeansMaxIterations = configuration.getInt("recommendation.engine.alg.kmeans.iterations");
		kMeansRuns = configuration.getInt("recommendation.engine.alg.kmeans.runs");
		kMeansK = configuration.getInt("recommendation.engine.alg.kmeans.k");
	}

	@Override
	public List<VideoContent> recommendVideoContent(User user) {
		return null;
	}

	@Override
	public List<Activity> recommendActivity(User user) {
		return null;
	}

	@Override
	public void updateProfileRecord(Contact contact) {

		dataStore.add(contact);

	}

	@Override
	public void addDimension(Dimension dimension) {
		dimensions.add(dimension);
	}

	@Override
	public void train() {
		distData = sparkContext.parallelize(dataStore);
		points = distData.map(new ContactPoint(dimensions));	

		model = KMeans.train(points.rdd(), 2, 5, 1, KMeans.K_MEANS_PARALLEL());

		classify();
	}

	@Override
	public void classify() {
		// print out cluster centers
		Vector[] clusterCenters = model.clusterCenters();
		
		for(Contact contact : dataStore) {
			// get the feature vector for these contacts
			double[] point = ContactPoint.computePoint(contact, dimensions);
			Vector vector = Vectors.dense(point);
			// get the centroid idx this point is closest to
			int idx = model.predict(vector);
			Vector centroid = clusterCenters[idx];
			
			String groupId = String.valueOf(centroid.hashCode());
			log.info("groupId {}", groupId); // use hashcode for now
			
			// get the user for this contact and add the group to it's group member ship array
			User user = contact.getOwner();
			if(user != null) {
				user.getGroups().add(groupId);
			}
		}
		
	}












}
