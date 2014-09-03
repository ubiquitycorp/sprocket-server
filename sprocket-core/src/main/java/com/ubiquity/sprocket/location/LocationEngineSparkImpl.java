package com.ubiquity.sprocket.location;

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

import com.ubiquity.sprocket.domain.GroupMembership;
import com.ubiquity.sprocket.domain.Location;

public class LocationEngineSparkImpl implements LocationEngine {

	private Logger log = LoggerFactory.getLogger(getClass());

	private JavaSparkContext sparkContext;

	/**
	 * The distributed data set holding the feature vectors, representing the instance space
	 */
	private JavaRDD<Vector> points;

	/***
	 * The model we are training
	 */
	private KMeansModel model;
	private int kMeansMaxIterations;


	/***
	 * The distributed data set for holding location data
	 */
	private JavaRDD<Location> distData;

	public LocationEngineSparkImpl(Configuration configuration) {

		sparkContext = new JavaSparkContext(configuration.getString("location.engine.hadoop.master"), configuration.getString("location.engine.appname"));

		// create a distributed dataset based on an empty array
		distData = sparkContext.parallelize(new LinkedList<Location>());

		kMeansMaxIterations = configuration.getInt("recommendation.engine.alg.kmeans.iterations");

	}



	@Override
	public void updateLocationRecords(List<Location> loci) {
		distData = distData.union(sparkContext.parallelize(loci));
		distData.cache(); // cache it

		//log.info("dist data {}", distData.count());
	}

	@Override
	public GroupMembership assign(Location location) {
		double[] point = LocationFunction.computePoint(location);
		Vector vector = Vectors.dense(point);
		// get the centroid idx this point is closest to
		int idx = model.predict(vector);
		String groupIdentifier = String.valueOf(idx);
		return new GroupMembership(null, location.getUser(), groupIdentifier);
	}

	@Override
	public void map() {
		// map all the points
		points = distData.map(new LocationFunction());

		// build model based on what's in the instance space now, with k determined as the rule of thumb
		long k = Math.round(Math.sqrt(points.count() / (double)2));
		log.info("dist data {}", points.count());

		log.info("K={}, max {}", k, kMeansMaxIterations);
		model = KMeans.train(points.rdd(), (int)k, kMeansMaxIterations, 1, KMeans.K_MEANS_PARALLEL());
	}



	@Override
	public List<GroupMembership> assign(List<Location> loci) {
		List<GroupMembership> assignments = new LinkedList<GroupMembership>();
		for(Location locus : loci) {
			assignments.add(assign(locus));
		}
		return assignments;
	}














}
