package com.ubiquity.sprocket.datasync.worker.manager;

import java.util.List;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.identity.domain.Application;
import com.ubiquity.identity.domain.User;
import com.ubiquity.sprocket.datasync.handlers.ContactHandler;

/***
 * Handles the processing of each feed type
 * 
 * @author peter.tadros
 * 
 */
public class ContactsSyncProcessor extends SyncProcessor {

	private List<User> users;

	/**
	 * Starts a processor with the underlying list
	 * 
	 * @param block
	 * @param from
	 * @param to
	 */
	public ContactsSyncProcessor(List<User> users) {
		log.info("Created ContactSyncProcessor for users {}", users);
		this.users = users;
		createChainHandelrs();
	}

	/***
	 * Creates a data sync processor that operate
	 */
	public ContactsSyncProcessor() {
		log.info("Created ContactSyncProcessor");
		createChainHandelrs();
	}

	private void createChainHandelrs() {
		mainHandler = new ContactHandler(this);
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
			Long startTime, endTime;
			startTime = System.currentTimeMillis();
			for (User user : users) {
				numRefreshed += syncDataForUser(user,application );
			}
			endTime = System.currentTimeMillis();
			log.info("{}: Periodic Sync completed in {} seconds", Thread
					.currentThread().getName(), (endTime - startTime) / 1000);
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
		
		return numRefreshed;
	}
}
