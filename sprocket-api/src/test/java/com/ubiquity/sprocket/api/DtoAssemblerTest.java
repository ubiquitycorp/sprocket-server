package com.ubiquity.sprocket.api;

import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.ubiquity.identity.domain.User;
import com.ubiquity.integration.domain.Contact;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.Message;
import com.ubiquity.integration.factory.TestMessageFactory;
import com.ubiquity.sprocket.api.dto.model.MessageDto;

/***
 * Tests the routines that assemble entity object into api dto objects that have special processing or logic that needs to 
 * be validated
 * 
 * @author chris
 *
 */
public class DtoAssemblerTest {
	
	@Test
	public void testAssembleMessageConversation() {
		
		List<Message> conversation = TestMessageFactory.createConversation(ExternalNetwork.Facebook);
		// get the owner and create one orphan message
		
		Message aMessage = conversation.get(0);
		User owner = aMessage.getOwner();
		Contact sender = aMessage.getSender();
		String conversationIdentifier = UUID.randomUUID().toString();
		conversation.add(TestMessageFactory.createMessageWithMininumRequiredFields(owner, sender, ExternalNetwork.Facebook, conversationIdentifier));
		
		
		List<MessageDto> messageDtoList = DtoAssembler.assemble(conversation);
		
	
		// There should only be one DTO
		Assert.assertTrue(messageDtoList.size() == 2);
		// make sure the ordering is correct and that we have 2 conversation items
		MessageDto latestMessageDto = messageDtoList.get(0);
		// and 2 conversation items
		Assert.assertTrue(latestMessageDto.getConversation().size() == 3);
		latestMessageDto.getConversation().getFirst();
		// test that the next message in the conversation was after the top message
//		Assert.assertTrue(latestMessageDto.getDate().longValue() > nextMessageDto.getDate().longValue());
//		MessageDto lastMessageDto = latestMessageDto.getConversation().getLast();
//		// test the last message was later than the
//		Assert.assertTrue(nextMessageDto.getDate().longValue() > lastMessageDto.getDate().longValue());
		
	}

}
