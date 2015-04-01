package com.ubiquity.sprocket.analytics.worker.jobs;

import org.joda.time.Duration;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssignmentSyncJob implements Job {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			long start = System.currentTimeMillis();
			log.info("Executing assignment sync job start {}", start);
			
			
			// this is where the recommendation engine would run and assign groups
			
			long finish = System.currentTimeMillis();
			log.info("Finished assignment sync job {}, duration: {}", finish, new Duration(finish));

		} catch (Exception e) {
			log.error("Could not process sync job: {}", e);
		}
	}
	
	
	
}
