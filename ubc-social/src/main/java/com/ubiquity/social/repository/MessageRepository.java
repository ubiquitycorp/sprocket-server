package com.ubiquity.social.repository;

import java.util.List;

import com.niobium.repository.Repository;
import com.ubiquity.social.domain.Message;

/***
 * 
 * Interface exposing CRUD methods for the message entity
 * 
 * @author chris
 *
 */
public interface MessageRepository extends Repository <Long, Message> {
	/***
	 * Finds all messages
	 * 
	 * @param ownerId
	 * 
	 * @return
	 */
	List<Message> findByOwnerId(Long ownerId);

}

