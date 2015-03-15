package com.ubiquity.sprocket.datasync.worker.master.jobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.messaging.format.Message;
import com.ubiquity.sprocket.messaging.MessageConverterFactory;
import com.ubiquity.sprocket.messaging.MessageQueueFactory;
import com.ubiquity.sprocket.messaging.definition.ActiveUsersFound;
import com.ubiquity.sprocket.service.ServiceFactory;

public class DataSyncJob implements Job {
	private Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		log.debug("Executing Data sync");
		try {
			// get the size of a processing block
			int maxBlockSize = context.getJobDetail().getJobDataMap()
					.getInt("blockSize");

			// get total number of users; the number of threads is equal to
			// users / block size
			List<Long[]> users = ServiceFactory.getUserService()
					.findAllActiveSprocketUserIds();
			
			log.info("creating synchronization data for {} users", users.size());

			// get the start/end block identifiers
			// int i;
			// int start = 0;
			// int end = 0;
			// for (i = 0; i < syncMessagesNum; i++) {
			// start = i == 0 ? 0 : end;
			// end += blockSize;
			// log.info("start {}, end {}", start, end);
			// groupUsersByapplicationID(users.subList(start, end));
			// }
			//
			// // get the remainder for the last thread
			// int remainder = numUsers % blockSize;
			// start = end;
			// end = start + remainder;
			// log.info("start {}, end {}", start, end);
			// groupUsersByapplicationID(users.subList(start, end));
			if (users.size() == 0)
				return;
			Long currentApplicationID = null;
			List<Long> userIds = new ArrayList<Long>();
			int currntBlockSize = 0;
			for (Long[] userapplication : users) {
				
				if(currntBlockSize ==0 )
					currentApplicationID = userapplication[1];
				
				if (!currentApplicationID.equals(userapplication[1])
						|| currntBlockSize == maxBlockSize) {
					sendSyncActiveUsersMessage(userIds, currentApplicationID);
					userIds = new ArrayList<Long>();
					currntBlockSize =0;
				} else {
					userIds.add(userapplication[0]);
					currntBlockSize++;
				}
			}

		} catch (Exception e) {
			log.error("Could not process message: {}", e);
		}
	}

	private void sendSyncActiveUsersMessage(List<Long> userIds,
			Long applicationID) throws IOException {
		ActiveUsersFound content = new ActiveUsersFound(userIds, applicationID);

		// serialize and send it
		String message = MessageConverterFactory.getMessageConverter()
				.serialize(new Message(content));
		MessageQueueFactory.getCacheInvalidationQueueProducer().write(
				message.getBytes());
	}
}
