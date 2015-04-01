package com.ubiquity.sprocket.datasync.handlers;

import java.util.EnumSet;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;

import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.ExternalNetworkApplication;
import com.ubiquity.integration.api.exception.AuthorizationException;
import com.ubiquity.integration.domain.Contact;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.service.ContactService;
import com.ubiquity.sprocket.datasync.worker.manager.ResourceType;
import com.ubiquity.sprocket.datasync.worker.manager.SyncProcessor;
import com.ubiquity.sprocket.domain.ConfigurationRules;
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
				ExternalNetwork.LinkedIn, ExternalNetwork.Google,
				ExternalNetwork.SocailMockNetwork);
	}

	@Override
	protected void syncData(ExternalIdentity identity, ExternalNetwork network,
			ExternalNetworkApplication externalNetworkApplication) {
		Long userId = identity.getUser().getUserId();
		// check if syncing contacts is enabled or not
		if (makeDecision(ConfigurationRules.contactsEnabled, network)) {
			// Sync contacts
			int n = processContacts(identity, network,
					externalNetworkApplication);
			processor.sendStepCompletedMessageToIndividual(backchannel,
					network, "Synchronized contacts", processor
							.getResoursePath(userId, network,
									ResourceType.contacts), n, userId,
					ResourceType.contacts);
		}
	}

	private int processContacts(ExternalIdentity identity,
			ExternalNetwork network,
			ExternalNetworkApplication externalNetworkApplication) {
		List<Contact> synced = null;
		DateTime start = new DateTime();
		Long userId = identity.getUser().getUserId();
		int size = -1;
		try {
			ContactService contactService = ServiceFactory.getContactService();
			synced = contactService.syncContacts(identity,
					externalNetworkApplication);
			// index for searching

			// log.debug(" indexing activities for identity {}", identity);

		} catch (AuthorizationException e) {
			identity.setIsActive(false);
			ServiceFactory.getExternalIdentityService().update(identity);
			log.error(" Could not process contacts for identity: {}",
					ExceptionUtils.getStackTrace(e), identity);
		} catch (Exception e) {
			log.error("{}: Could not process contacts for identity: {}",
					ExceptionUtils.getStackTrace(e), identity);
		} finally {
			size = (synced == null) ? -1 : synced.size();
			log.debug(
					" Processed {} contacts in {} seconds for user " + userId,
					size, new Period(start, new DateTime()).getSeconds());
		}

		return size;
	}
}
