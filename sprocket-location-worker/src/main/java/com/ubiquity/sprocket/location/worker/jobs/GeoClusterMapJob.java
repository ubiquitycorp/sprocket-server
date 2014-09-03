package com.ubiquity.sprocket.location.worker.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.sprocket.service.ServiceFactory;

public class GeoClusterMapJob implements Job {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("Executing geo cluster map");
		try {
			ServiceFactory.getLocationService().assignGeoClusters();
		} catch (Exception e) {
			log.error("Could not process sync job: {}", e);
		}
	}
	
}
