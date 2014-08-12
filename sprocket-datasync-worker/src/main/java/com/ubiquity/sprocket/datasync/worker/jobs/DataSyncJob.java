package com.ubiquity.sprocket.datasync.worker.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.sprocket.datasync.worker.manager.DataSyncManager;

public class DataSyncJob implements Job{
	private Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		log.debug("Executing contact sync");
		try {
			DataSyncManager manager = new DataSyncManager();
			manager.syncData();
		} catch (Exception e) {
			log.error("Could not process message: {}", e);
		}
	}

}
