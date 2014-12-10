package com.ubiquity.sprocket.analytics.worker.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecommendationSyncJob implements Job {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			long start = System.currentTimeMillis();
			log.info("Executing recommendation cluster job start {}", start);
			
//			ServiceFactory.getAnalyticsService().refreshProfileRecords();
//			ServiceFactory.getAnalyticsService().assignGroupsAndCreateRecommendedContent();
//			long finish = System.currentTimeMillis();
//			log.info("Finished recommendation cluster job {}, duration: {}", finish, new Duration(finish));

		} catch (Exception e) {
			log.error("Could not process sync job: {}", e);
		}
	}
	
	
	
}
