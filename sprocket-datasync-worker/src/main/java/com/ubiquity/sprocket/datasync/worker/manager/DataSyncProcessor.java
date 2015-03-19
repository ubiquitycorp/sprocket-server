package com.ubiquity.sprocket.datasync.worker.manager;

import java.util.List;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.identity.domain.Application;
import com.ubiquity.identity.domain.User;
import com.ubiquity.sprocket.datasync.handlers.ActivityHandler;
import com.ubiquity.sprocket.datasync.handlers.ContactHandler;
import com.ubiquity.sprocket.datasync.handlers.Handler;
import com.ubiquity.sprocket.datasync.handlers.LocalActivityHandler;
import com.ubiquity.sprocket.datasync.handlers.MessageHandler;
import com.ubiquity.sprocket.datasync.handlers.VideoHandler;
import com.ubiquity.sprocket.datasync.worker.manager.SyncProcessor;
import com.ubiquity.sprocket.domain.SprocketUser;

/***
 * Handles the processing of each feed type
 * 
 * @author mina, peter
 * 
 */
public class DataSyncProcessor extends SyncProcessor {

	private List<SprocketUser> users;
	
	SyncNotificationSender notificationProcessor;

	/**
	 * Starts a processor with the underlying list
	 * 
	 * @param block
	 * @param from
	 * @param to
	 */
	public DataSyncProcessor(List<SprocketUser> users) {
		log.info("Created DataSycnProcessor for users {}", users);
		this.users = users;
		createChainHandelrs();
		notificationProcessor = new SyncNotificationSender(mainHandler.getNext().getProcessedMessages());
	}

	/***
	 * Creates a data sync processor that operate
	 */
	public DataSyncProcessor(Boolean syncContacts) {
		log.info("Created DataSyncProcessor");
		this.syncContacts = syncContacts;
		createChainHandelrs();
		notificationProcessor = new SyncNotificationSender(mainHandler.getNext().getProcessedMessages());
	}

	private void createChainHandelrs() {
		mainHandler = new ActivityHandler(this);
		Handler messageHandler = new MessageHandler(this);
		Handler localActivityHandler = new LocalActivityHandler(this);
		Handler videoHandler = new VideoHandler(this);
		
		mainHandler.setNext(messageHandler);
		messageHandler.setNext(localActivityHandler);
		localActivityHandler.setNext(videoHandler);
		
		if(syncContacts)
		{
			Handler contactHandler = new ContactHandler(this);
			videoHandler.setNext(contactHandler);
		}
	}

	/***
	 * Refresh data of all users in all social networks
	 * 
	 * @return
	 */
	@Override
	public int syncData(Application application) {

		int numRefreshed = 0;

		try {
			notificationProcessor.start();
			Long startTime, endTime;
			startTime = System.currentTimeMillis();
			for (User user : users) {
				numRefreshed += syncDataForUser((SprocketUser)user,application);
			}
			endTime = System.currentTimeMillis();
			log.info("{}: Periodic Sync completed in {} seconds", Thread
					.currentThread().getName(), (endTime - startTime) / 1000);
		} finally {
			EntityManagerSupport.closeEntityManager();
			notificationProcessor.setTerminate();
		}
		
		return numRefreshed;
	}
	
}
