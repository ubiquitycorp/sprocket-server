package com.ubiquity.sprocket.datasync.worker.jobs;

import java.util.LinkedList;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.common.thread.ThreadPool;
import com.ubiquity.identity.domain.User;
import com.ubiquity.sprocket.datasync.worker.manager.DataSyncProcessor;
import com.ubiquity.sprocket.service.ServiceFactory;

public class DataSyncJob implements Job{
	private Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.debug("Executing contact sync");
		try {		    
			// get the size of a processing block
			int blockSize = context.getJobDetail().getJobDataMap().getInt("blockSize");
			
			// get total number of users; the number of threads is equal to users / block size
			List<User> users = ServiceFactory.getUserService().findAllActiveUsers();
			int numUsers = users.size();
			long numThreads = numUsers / blockSize;
			
			log.info("creating {} processor threads for {} users", numThreads + 1, numUsers);
			
			// create list of processors
			List<DataSyncProcessor> processors = new LinkedList<DataSyncProcessor>();
						
			// get the start/end block identifiers
			int i;
			int start = 0;
			int end = 0;
			for(i = 0; i < numThreads; i++) {
				start = i == 0 ? 0 : end + 1;
				end = end + blockSize;
				log.info("start {}, end {}", start, end);
				processors.add(new DataSyncProcessor(users, start, end));

			}
			
			// get the remainder for the last thread
			int remainder = numUsers % blockSize;
			start = end + 1;
			end = end + remainder;
			log.info("start {}, end {}", start, end);
			processors.add(new DataSyncProcessor(users, start, end));

		
			// start the thread pool, 10 consumer threads
			ThreadPool<DataSyncProcessor> threadPool = new ThreadPool<DataSyncProcessor>();
			threadPool.start(processors);
			
		} catch (Exception e) {
			log.error("Could not process message: {}", e);
		}
	}

}
