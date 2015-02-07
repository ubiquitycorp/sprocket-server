package com.ubiquity.sprocket.datasync.worker.master.jobs;

import java.io.IOException;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.messaging.format.Message;
import com.ubiquity.sprocket.messaging.MessageConverterFactory;
import com.ubiquity.sprocket.messaging.MessageQueueFactory;
import com.ubiquity.sprocket.messaging.definition.ContactsSync;
import com.ubiquity.sprocket.service.ServiceFactory;

public class ContactsSyncJob implements Job {
	private Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		log.debug("Executing contact sync");
		try {
			// get the size of a processing block
			int blockSize = context.getJobDetail().getJobDataMap()
					.getInt("blockSize");

			// get list of active users; the number of threads is equal to
			// users / block size
			List<Long> userIds = ServiceFactory.getUserService()
					.findAllActiveUserIds();
			int numUsers = userIds.size();
			long syncMessagesNum = numUsers / blockSize;

			log.info("creating {} synchronization contacts for {} users",
					syncMessagesNum + 1, numUsers);

			// get the start/end block identifiers
			int i;
			int start = 0;
			int end = 0;
			for (i = 0; i < syncMessagesNum; i++) {
				start = i == 0 ? 0 : end;
				end += blockSize;
				log.info("start {}, end {}", start, end);
				sendSyncContactsMessage(userIds.subList(start, end));
			}

			// get the remainder for the last thread
			int remainder = numUsers % blockSize;
			start = end;
			end = start + remainder;
			log.info("start {}, end {}", start, end);
			sendSyncContactsMessage(userIds.subList(start, end));

		} catch (Exception e) {
			log.error("Could not process message: {}", e);
		}
	}

	private void sendSyncContactsMessage(List<Long> userIds) throws IOException {
		if(userIds.size()==0)
			return;
		ContactsSync content = new ContactsSync(userIds);

		// serialize and send it
		String message = MessageConverterFactory.getMessageConverter()
				.serialize(new Message(content));
		MessageQueueFactory.getCacheInvalidationQueueProducer().write(
				message.getBytes());
	}
}
