package com.ubiquity.sprocket.analytics.recommendation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.configuration.Configuration;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.clustering.KMeans;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.sprocket.domain.GroupMembership;
import com.ubiquity.sprocket.domain.Location;

public class RecommendationEngineSparkImpl implements RecommendationEngine {

	private Logger log = LoggerFactory.getLogger(getClass());

	private static final String GLOBAL_CONTEXT_IDENTIFIER = UUID.randomUUID()
			.toString();

	private Map<String, ExecutionContext> contextMap = new HashMap<String, ExecutionContext>();

	private JavaSparkContext sparkContext;

	/***
	 * The distributed dataset for holding global profile data
	 */
	private JavaRDD<Profile> distData;

	public RecommendationEngineSparkImpl(Configuration configuration) {

		sparkContext = new JavaSparkContext(
				configuration.getString("recommendation.engine.hadoop.master"),
				configuration.getString("recommendation.engine.appname"));

		// create a distributed dataset based on an empty array
		distData = sparkContext.parallelize(new LinkedList<Profile>());

		// create the global context
		contextMap.put(GLOBAL_CONTEXT_IDENTIFIER, new ExecutionContext(null,
				configuration));
	}

	@Override
	public void addDimension(Dimension dimension) {
		ExecutionContext context = contextMap.get(GLOBAL_CONTEXT_IDENTIFIER);
		context.addDimension(dimension);
	}

	@Override
	public void train() {
		ExecutionContext context = contextMap.get(GLOBAL_CONTEXT_IDENTIFIER);
		context.train();
	}

	@Override
	public void updateProfileRecords(List<Profile> profiles) {
		distData = distData.union(sparkContext.parallelize(profiles));
		distData.cache(); // cache it

		log.info("dist data {}", distData.count());
	}

	@Override
	public List<GroupMembership> assign(Profile profile) {
		ExecutionContext context = contextMap.get(GLOBAL_CONTEXT_IDENTIFIER);
		return Arrays.asList(new GroupMembership[] { context.assign(profile) });
	}

	@Override
	public void addDimension(Dimension dimension, ExternalNetwork context) {
		getExecutionContext(context).addDimension(dimension);
	}

	@Override
	public void train(ExternalNetwork context) {
		ExecutionContext executionContext = getExecutionContext(context);
		if (executionContext == null)
			throw new IllegalArgumentException("No context to train");
		executionContext.train();
	}

	@Override
	public void addContext(ExternalNetwork context, Configuration configuration) {
		contextMap.put(context.toString(), new ExecutionContext(context, configuration));
	}

	/**
	 * Returns execution context
	 * 
	 * @param name
	 * 
	 * @return context
	 * 
	 * @throws IllegalArgumentException
	 *             if context does not exist
	 */
	private ExecutionContext getExecutionContext(ExternalNetwork network) {
		ExecutionContext context = contextMap.get(network.toString());
		if (context == null)
			throw new IllegalArgumentException(
					"No execution context exists by that name: " + network.toString());
		return context;

	}

	private class ExecutionContext {

		/**
		 * The distributed dataset holding the feature vectors, representing the
		 * instance space
		 */
		private JavaRDD<Vector> points;

		private List<Dimension> dimensions = new ArrayList<Dimension>();

		private ExternalNetwork context;

		/***
		 * The model we are training
		 */
		private KMeansModel model;
		private int kMeansMaxIterations;

		protected ExecutionContext(ExternalNetwork context, Configuration configuration) {
			this.context = context;

			kMeansMaxIterations = configuration
					.getInt("recommendation.engine.alg.kmeans.iterations");
		}

		protected void addDimension(Dimension dimension) {
			dimensions.add(dimension);
		}

		protected void updateDimension(Dimension dimension) {
			dimensions.remove(dimension);
			dimensions.add(dimension);
		}

		protected GroupMembership assign(Profile profile) {
			double[] point = ProfileFunction.computePoint(profile, dimensions);
			Vector vector = Vectors.dense(point);

			// get the centroid idx this point is closest to
			int idx = model.predict(vector);
			String groupIdentifier = String.valueOf(idx);

			// only create a membership assignment for a registered user
			return new GroupMembership(context, profile.getUser(), groupIdentifier);
		}

		
		protected GroupMembership assign(Contact contact,
				Location location) {

			log.info("cluster centers: {} ", model.clusterCenters());

			// get the feature vector for these contacts
			double[] point = ProfileFunction.computePoint(contact,
					location, dimensions);
			Vector vector = Vectors.dense(point);

			// get the centroid idx this point is closest to
			int idx = model.predict(vector);
			String groupIdentifier = String.valueOf(idx);

			// only create a membership assignment for a registered user
			if (contact.getOwner() != null)
				return new GroupMembership(context, contact
						.getOwner(), groupIdentifier);
			
			return null;

		}

		protected void train() {
			
			if(context != null)
				points = distData.map(new ContactFunction(context, dimensions));
			else 
				points = distData.map(new ProfileFunction(dimensions));

			// build model based on what's in the instance space now, with k
			// determined as the rule of thumb
			long k = Math.round(Math.sqrt(points.count() / (double) 2));

			model = KMeans.train(points.rdd(), (int) k, kMeansMaxIterations, 1,
					KMeans.K_MEANS_PARALLEL());

		}
	}

	@Override
	public void updateDimension(Dimension dimension) {
		ExecutionContext context = contextMap.get(GLOBAL_CONTEXT_IDENTIFIER);
		context.updateDimension(dimension);	
	}

	@Override
	public void updateDimension(Dimension dimension, ExternalNetwork context) {
		getExecutionContext(context).updateDimension(dimension);
	}

	@Override
	public void clear() {
		distData.unpersist();
	}

	@Override
	public long size() {
		return distData.count();
	}

	@Override
	public List<GroupMembership> assign(Profile profile, ExternalNetwork context) {
		ExecutionContext executionContext = getExecutionContext(context);
		if (executionContext == null)
			throw new IllegalArgumentException("No context: " + context);
		// make sure the user has a profile for this context; if not, return an empty assignment list
		Contact contact = profile.getContactForExternalNetwork(context);
		if(contact == null)
			return new LinkedList<GroupMembership>();
		return Arrays.asList(new GroupMembership[] { executionContext.assign(contact,
				profile.getLocation()) });
	}

}
