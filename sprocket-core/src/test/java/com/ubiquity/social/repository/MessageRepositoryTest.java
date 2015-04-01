package com.ubiquity.social.repository;

import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.factory.TestUserFactory;
import com.ubiquity.identity.repository.UserRepository;
import com.ubiquity.identity.repository.UserRepositoryJpaImpl;
import com.ubiquity.integration.domain.Contact;
import com.ubiquity.integration.domain.Conversation;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.Message;
import com.ubiquity.integration.factory.TestContactFactory;
import com.ubiquity.integration.repository.ContactRepository;
import com.ubiquity.integration.repository.ContactRepositoryJpaImpl;
import com.ubiquity.integration.repository.MessageRepository;
import com.ubiquity.integration.repository.MessageRepositoryJpaImpl;

/***
 * Tests testing basic CRUD operations for a user repository
 * 
 * @author chris
 *
 */
public class MessageRepositoryTest {

	private MessageRepository messageRepository;
	private UserRepository userRepository;
	private ContactRepository contactRepository;
	
	private Message message;
	private Contact sender;
	private User owner;

	@After
	public void tearDown() throws Exception {
		EntityManagerSupport.closeEntityManager();
	}

	@Before
	public void setUp() throws Exception {
		messageRepository = new MessageRepositoryJpaImpl();
		userRepository = new UserRepositoryJpaImpl();
		contactRepository = new ContactRepositoryJpaImpl();

		
		owner = TestUserFactory.createTestUserWithMinimumRequiredProperties(null);
		
		EntityManagerSupport.beginTransaction();
		userRepository.create(owner);
		EntityManagerSupport.commit();
					
		sender = TestContactFactory.createContactWithMininumRequiredFieldsAndExternalNetwork(owner, ExternalNetwork.Facebook);
		
		EntityManagerSupport.beginTransaction();
		contactRepository.create(sender);
		EntityManagerSupport.commit();
		
		String conversationIdentifier = UUID.randomUUID().toString();
		Conversation conversation = new Conversation.Builder()
								.conversationIdentifier(conversationIdentifier)
								.externalNetwork(ExternalNetwork.Facebook)
								.build();
		// now create a message
		message  = new Message.Builder()
			.title(UUID.randomUUID().toString())
			.body(UUID.randomUUID().toString())
			.sentDate(System.currentTimeMillis())
			.sender(sender)
			.lastUpdated(System.currentTimeMillis())
			.conversation(conversation)
			.externalNetwork(ExternalNetwork.Facebook)
			.externalIdentifier(UUID.randomUUID().toString())
			.owner(owner)
			.externalNetwork(ExternalNetwork.Facebook)
			.build();
		
		saveMessage(message);

		
		
	}

	@Test
	public void testCreate() throws Exception {
		Message persisted = messageRepository.read(message.getMessageId());
		Assert.assertNotNull(persisted.getMessageId());
		Assert.assertNotNull(persisted.getSender());
		Assert.assertEquals(message.getTitle(), persisted.getTitle());
		Assert.assertEquals(message.getBody(), persisted.getBody());
	}
	
	@Test
	public void testFindByOwner() throws Exception {
		List<Message> allMessages = messageRepository.findByOwnerId(owner.getUserId());
		Assert.assertFalse(allMessages.isEmpty());
		Message persisted = allMessages.get(0);
		Assert.assertTrue(persisted.getMessageId().longValue() == message.getMessageId().longValue());
	}
	
	@Test
	public void testConversationGroupingAndMessageOrdering() {
		
		String conversationIdentifier = UUID.randomUUID().toString();
		Conversation conversation = new Conversation.Builder()
								.conversationIdentifier(conversationIdentifier)
								.externalNetwork(ExternalNetwork.Facebook)
								.build();
		message.setConversation(conversation);
		
		
		// add 2 messages with a conversation identifier
		Message first  = new Message.Builder()
			.title(UUID.randomUUID().toString())
			.body(UUID.randomUUID().toString())
			.conversation(conversation)
			.sentDate(System.currentTimeMillis())
			.sender(sender)
			.lastUpdated(System.currentTimeMillis())
			.externalNetwork(ExternalNetwork.Facebook)
			.externalIdentifier(UUID.randomUUID().toString())
			.owner(owner)
			.externalNetwork(ExternalNetwork.Facebook)
			.build();
		saveMessage(first);
		
		Message next  = new Message.Builder()
			.title(UUID.randomUUID().toString())
			.body(UUID.randomUUID().toString())
			.conversation(conversation)
			.sentDate(System.currentTimeMillis() + 1)
			.sender(sender)
			.externalNetwork(ExternalNetwork.Facebook)
			.externalIdentifier(UUID.randomUUID().toString())
			.owner(owner)
			.lastUpdated(System.currentTimeMillis())
			.externalNetwork(ExternalNetwork.Facebook)
			.build();
		saveMessage(next);

		// now query, making sure messages are grouped properly
		List<Message> messages = messageRepository.findByOwnerIdAndSocialNetwork(owner.getUserId(), ExternalNetwork.Facebook);
		Assert.assertTrue(messages.size() == 3);
		
		// let's make sure this one has no id
		Message persisted = messages.get(0);
		Assert.assertNotNull(persisted.getConversation().getConversationIdentifier());
		
		// test sorting and that they have the same conversation
		persisted = messages.get(1);
		Assert.assertTrue(persisted.getMessageId().longValue() == first.getMessageId().longValue());
		Assert.assertNotNull(persisted.getConversation().getConversationIdentifier());
		Assert.assertTrue(persisted.getConversation().getConversationIdentifier().equals(first.getConversation().getConversationIdentifier()));
		
		persisted = messages.get(2);
		Assert.assertTrue(persisted.getMessageId().longValue() == next.getMessageId().longValue());
		Assert.assertNotNull(persisted.getConversation().getConversationIdentifier());
		Assert.assertTrue(persisted.getConversation().getConversationIdentifier().equals(first.getConversation().getConversationIdentifier()));

	}
	
	private void saveMessage(Message message) {
		EntityManagerSupport.beginTransaction();
		messageRepository.create(message);
		EntityManagerSupport.commit();
	}
	
	@Test
	public void testFindByExternalIdentifier() throws Exception {
		Message persisted = messageRepository.getByExternalIdentifierAndSocialNetwork(message.getExternalIdentifier(), owner.getUserId(), ExternalNetwork.Facebook);;
		Assert.assertNotNull(persisted);
		Assert.assertTrue(persisted.getMessageId().longValue() == message.getMessageId().longValue());
		
		// query by different id
		persisted = messageRepository.getByExternalIdentifierAndSocialNetwork(UUID.randomUUID().toString(), owner.getUserId(), ExternalNetwork.Facebook);;
		Assert.assertNull(persisted);
		
		// query by same id, different network
		persisted = messageRepository.getByExternalIdentifierAndSocialNetwork(message.getExternalIdentifier(), owner.getUserId(), ExternalNetwork.Facebook);;
		Assert.assertNotNull(persisted);
				
	}


}
