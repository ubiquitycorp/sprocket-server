package com.ubiquity.sprocket.datasync.handlers;

import java.util.EnumSet;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;

import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.integration.api.exception.AuthorizationException;
import com.ubiquity.integration.domain.Contact;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.service.ContactService;
import com.ubiquity.sprocket.datasync.worker.manager.ResourceType;
import com.ubiquity.sprocket.datasync.worker.manager.SyncProcessor;
import com.ubiquity.sprocket.service.ServiceFactory;

/***
 * 
 * @author peter.tadros
 * 
 */
public class ContactHandler extends Handler {

	public ContactHandler(SyncProcessor processor) {
		super(processor);
		networks = EnumSet.of(ExternalNetwork.Facebook,
				ExternalNetwork.LinkedIn, ExternalNetwork.Google);
	}

	@Override
	protected void syncData(ExternalIdentity identity, ExternalNetwork network) {
		Long userId = identity.getUser().getUserId();
		// Sync activities
		int n = processContacts(identity, network);
		processor.sendStepCompletedMessageToIndividual(backchannel, network,
				"Synchronized contacts", processor.getResoursePath(userId,
						network, ResourceType.contacts), n, userId,
				ResourceType.contacts);
	}

	private int processContacts(ExternalIdentity identity,
			ExternalNetwork network) {
		List<Contact> synced = null;
		DateTime start = new DateTime();
		Long userId = identity.getUser().getUserId();

		try {
			ContactService contactService = ServiceFactory.getContactService();
			synced = contactService.syncContacts(identity);
			// index for searching

			// log.debug(" indexing activities for identity {}", identity);
			return synced.size();
		} catch (AuthorizationException e) {
			ServiceFactory.getSocialService().setActiveNetworkForUser(userId,
					network, false);
			log.error(" Could not process contacts for identity: {}",
					ExceptionUtils.getStackTrace(e));
			return -1;
		} catch (Exception e) {
			log.error("{}: Could not process contacts for identity: {}",
					ExceptionUtils.getStackTrace(e));
			return -1;
		} finally {
			int n = (synced == null) ? -1 : synced.size();
			log.debug(
					" Processed {} contacts in {} seconds for user " + userId,
					n, new Period(start, new DateTime()).getSeconds());
		}
	}

}
