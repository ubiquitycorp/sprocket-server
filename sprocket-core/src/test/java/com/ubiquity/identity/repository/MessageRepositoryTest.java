package com.ubiquity.identity.repository;

import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.NativeIdentity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.media.domain.Image;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Message;
import com.ubiquity.social.domain.SocialNetwork;
import com.ubiquity.social.repository.ContactRepository;
import com.ubiquity.social.repository.ContactRepositoryJpaImpl;
import com.ubiquity.social.repository.MessageRepository;
import com.ubiquity.social.repository.MessageRepositoryJpaImpl;

/***
 * Tests testing basic CRUD operations for a user repository
 * 
 * @author chris
 *
 */
public class MessageRepositoryTest {

	private MessageRepository messageRepository;
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

		UserRepository userRepository = new UserRepositoryJpaImpl();
		owner = new User.Builder()
				.lastUpdated(System.currentTimeMillis())
				.firstName(UUID.randomUUID().toString())
				.lastName(UUID.randomUUID().toString())
				.email(UUID.randomUUID().toString())
				.clientPlatform(ClientPlatform.Android)
				.displayName(UUID.randomUUID().toString())
				.build();
		userRepository.create(owner);
		
		NativeIdentity identity = new NativeIdentity.Builder()
			.isActive(Boolean.TRUE)
			.lastUpdated(System.currentTimeMillis())
			.user(owner)
			.username(UUID.randomUUID().toString())
			.password(UUID.randomUUID().toString())
			.build();
		owner.getIdentities().add(identity);
		
		EntityManagerSupport.beginTransaction();
		userRepository.create(owner);
		EntityManagerSupport.commit();
		
		// now create a contact - who isn't necessarily a user
		sender = new Contact.Builder().lastUpdated(System.currentTimeMillis())
						.displayName("Jill").firstName("Jill").lastName("Jackson")
						.email("jill@mail.com").profileUrl("http://jills.profile.link")
						.image(new Image("http://jills.image.url"))
						.externalIdentity(new ExternalIdentity.Builder()
							.identifier(UUID.randomUUID().toString())
							.isActive(Boolean.TRUE)
							.lastUpdated(System.currentTimeMillis())
							.identityProvider(SocialNetwork.Facebook.getValue())
							.build())
						.owner(owner)
						
						.build();
			
		ContactRepository contactRepository = new ContactRepositoryJpaImpl();
		EntityManagerSupport.beginTransaction();
		contactRepository.create(sender);
		EntityManagerSupport.commit();
		
		// now create a message
		message  = new Message.Builder()
			.title(UUID.randomUUID().toString())
			.body(UUID.randomUUID().toString())
			.sentDate(System.currentTimeMillis())
			.sender(sender)
			.owner(owner)
			.build();
		
		EntityManagerSupport.beginTransaction();
		messageRepository.create(message);
		EntityManagerSupport.commit();

		
		
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


}
