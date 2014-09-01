package com.ubiquity.sprocket.analytics.worker.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.sprocket.service.ServiceFactory;

public class RecommendationSyncJob implements Job {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.debug("Executing event sync");
		try {
			ServiceFactory.getAnalyticsService().recommend();
		} catch (Exception e) {
			log.error("Could not process message: {}", e);
		}
	}
	
}
