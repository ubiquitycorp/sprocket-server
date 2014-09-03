package com.ubiquity.sprocket.analytics.recommendation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.configuration.Configuration;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.clustering.KMeans;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.sprocket.domain.GroupMembership;

public class RecommendationEngineSparkImpl implements RecommendationEngine {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	private static final String GLOBAL_CONTEXT_IDENTIFIER = UUID.randomUUID().toString();

	private Map<String, ExecutionContext> contextMap = new HashMap<String, ExecutionContext>();
	

	private JavaSparkContext sparkContext;

	/***
	 * The distributed dataset for holding contact data
	 */
	private JavaRDD<Contact> distData;

	public RecommendationEngineSparkImpl(Configuration configuration) {

		sparkContext = new JavaSparkContext(configuration.getString("recommendation.engine.hadoop.master"), configuration.getString("recommendation.engine.appname"));

		// create a distributed dataset based on an empty array
		distData = sparkContext.parallelize(new LinkedList<Contact>());
		
		// create the global context
		contextMap.put(GLOBAL_CONTEXT_IDENTIFIER, new ExecutionContext(null, configuration));
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
	public void updateProfileRecords(List<Contact> contacts) {

		distData = distData.union(sparkContext.parallelize(contacts));
		distData.cache(); // cache it

		log.info("dist data {}", distData.count());
	}

	@Override
	public List<GroupMembership> assign(List<Contact> contacts) {
		ExecutionContext context = contextMap.get(GLOBAL_CONTEXT_IDENTIFIER);
		return context.assign(contacts);
	}

	@Override
	public void addDimension(Dimension dimension, String context) {
		getExecutionContext(context).addDimension(dimension);
	}

	@Override
	public void train(String context) {
		getExecutionContext(context).train();
	}

	@Override
	public List<GroupMembership> assign(List<Contact> contacts, String context) {
		return getExecutionContext(context).assign(contacts);
	}

	@Override
	public void addContext(String context, Configuration configuration) {
		contextMap.put(context, new ExecutionContext(context, configuration));
	}
	
	/**
	 * Returns execution context
	 * 
	 * @param name
	 * 
	 * @return context
	 * 
	 * @throws IllegalArgumentException if context does not exist
	 */
	private ExecutionContext getExecutionContext(String name) {
		ExecutionContext context = contextMap.get(name);
		if(context == null)
			throw new IllegalArgumentException("No execution context exists by that name: " + name);
		return context;

	}
	
	private class ExecutionContext  {
		
		/**
		 * The distributed dataset holding the feature vectors, representing the instance space
		 */
		private JavaRDD<Vector> points;

		private List<Dimension> dimensions = new ArrayList<Dimension>();
		
		private ExternalNetwork externalNetwork;
		
		/***
		 * The model we are training
		 */
		private KMeansModel model;
		private int kMeansMaxIterations;
		
		protected ExecutionContext(String context, Configuration configuration) {
			if(context != null)
				externalNetwork = ExternalNetwork.valueOf(context);
			
			kMeansMaxIterations = configuration.getInt("recommendation.engine.alg.kmeans.iterations");
		}

		protected void addDimension(Dimension dimension) {
			dimensions.add(dimension);
		}
		
		protected void updateDimension(Dimension dimension) {
			dimensions.remove(dimension);
			dimensions.add(dimension);
		}
		
		
		protected List<GroupMembership> assign(List<Contact> contacts) {
			
			List<GroupMembership> membership = new LinkedList<GroupMembership>();
			// TODO Auto-generated method stub
			for(Contact contact : contacts) {
				// get the feature vector for these contacts
				double[] point = ContactFunction.computePoint(contact, dimensions);
				Vector vector = Vectors.dense(point);
				// get the centroid idx this point is closest to
				int idx = model.predict(vector);
				String groupIdentifier = String.valueOf(idx);
				
				// only create a membership assignment for a registered user
				if(contact.getOwner() != null)
					membership.add(new GroupMembership(externalNetwork, contact.getOwner(), groupIdentifier));
			}
			return membership;

		}
		
		protected void train() {
			// map all the points
			points = distData.map(new ContactFunction(dimensions));
			
			// build model based on what's in the instance space now, with k determined as the rule of thumb
			long k = Math.round(Math.sqrt(points.count() / (double)2));
			
			model = KMeans.train(points.rdd(), (int)k, kMeansMaxIterations, 1, KMeans.K_MEANS_PARALLEL());
		
		}
	}

	@Override
	public void updateDimension(Dimension dimension) {
		ExecutionContext context = contextMap.get(GLOBAL_CONTEXT_IDENTIFIER);
		context.updateDimension(dimension);
	}

	@Override
	public void updateDimension(Dimension dimension, String context) {
		getExecutionContext(context).updateDimension(dimension);
	}

	@Override
	public void clear() {
		distData.unpersist();		
	}














}
