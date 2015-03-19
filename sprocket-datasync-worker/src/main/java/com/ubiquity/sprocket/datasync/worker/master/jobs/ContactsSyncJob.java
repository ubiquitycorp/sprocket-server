package com.ubiquity.sprocket.datasync.worker.master.jobs;

import java.io.IOException;
import java.math.BigDecimal;
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
			int maxBlockSize = context.getJobDetail().getJobDataMap()
					.getInt("blockSize");

			// get total number of users; the number of threads is equal to
			// users / block size
			List<BigDecimal[]> users = ServiceFactory.getUserService()
					.findAllActiveSprocketUserIds();

			log.info("creating synchronization data for {} users", users.size());

			if (users.size() == 0)
				return;
			Long currentApplicationID = -1L;
			List<Long> userIds = new ArrayList<Long>();
			int currntBlockSize = 0;
			Long thisUser = null;
			Long thisApplication = null;
			for (Object[] userApplicationIds : users) {
				thisUser = Long.valueOf(userApplicationIds[0].toString());
				thisApplication = userApplicationIds[1] == null ? -1L : Long
						.valueOf(userApplicationIds[1].toString());

				if (!currentApplicationID.equals(thisApplication)
						|| currntBlockSize == maxBlockSize) {
					sendSyncContactsMessage(userIds,
							currentApplicationID == -1 ? null
									: currentApplicationID);
					userIds = new ArrayList<Long>();
					currntBlockSize = 0;
					currentApplicationID = thisApplication;

				}
				userIds.add(thisUser);
				currntBlockSize++;
			}

			if (currntBlockSize > 0) {
				sendSyncContactsMessage(userIds,
						currentApplicationID == -1 ? null
								: currentApplicationID);
			}
		} catch (Exception e) {
			log.error("Could not process message: {}", e);
		}
	}

	private void sendSyncContactsMessage(List<Long> userIds, Long applicationID)
			throws IOException {

		ContactsSync content = new ContactsSync(userIds, applicationID);

		// serialize and send it
		String message = MessageConverterFactory.getMessageConverter()
				.serialize(new Message(content));
		MessageQueueFactory.getCacheInvalidationQueueProducer().write(
				message.getBytes());
	}
}
