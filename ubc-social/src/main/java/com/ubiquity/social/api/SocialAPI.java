package com.ubiquity.social.api;

import java.util.List;

import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Event;
import com.ubiquity.social.domain.ExternalIdentity;
import com.ubiquity.social.domain.Message;

/***
 * Social interface for all social providers
 * 
 * @author Peter
 * 
 */
public interface SocialAPI {

	/***
	 * Authenticates the user, returning the contact
	 * 
	 * @param identity Social identity
	 * @return contact populated with data from the social network or null if there was no authentication
	 * 
	 * @throws IllegalArgumentException if the passed in identity does not match the provider
	 * @throws UnsupportedOperationException if the underlying social network does not support this action
	 * 
	 */
	Contact authenticateUser(ExternalIdentity identity);
	
	/***
	 * Finds a list of contacts for this identifier
	 * 
	 * @param identity Social identity
	 * @return  
	 * 
	 * @throws IllegalArgumentException if the passed in identity does not match the provider
	 * @throws UnsupportedOperationException if the underlying social network does not support this action
	 */
	List<Contact> findContactsByOwnerIdentity(ExternalIdentity identity);
	
	/***
	 * Returns a list of events the passed in contacts created
	 * 
	 * @param identity Social identity
	 * @param contacts that created the events
	 * 
	 * @return list of events created by the list of contacts
	 * 
	 * @throws UnsupportedOperationException if the underlying social network does not support this action
	 */
	List<Event> findEventsCreatedByContacts(ExternalIdentity identity, List<Contact> contacts);
	
	
	/***
	 * Post to a user's wall
	 * 
	 * @param fromIdentity
	 * @param toIdentity
	 * @param message
	 * 
	 * @return True if the post was a success
	 * 
	 * @throws UnsupportedOperationException if the underlying social network does not support this action
	 */
	Boolean postToWall(ExternalIdentity fromIdentity, ExternalIdentity toIdentity, String message);
	
	
	List<Message> listMessages(ExternalIdentity externalIdentity);

	List<Activity> listActivities(ExternalIdentity external);
	
	
}
