package com.ubiquity.identity.repository;

import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.factory.UserFactory;
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

		
		owner = UserFactory.createTestUserWithMinimumRequiredProperties();
		
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
			
		EntityManagerSupport.beginTransaction();
		contactRepository.create(sender);
		EntityManagerSupport.commit();
		
		// now create a message
		message  = new Message.Builder()
			.title(UUID.randomUUID().toString())
			.body(UUID.randomUUID().toString())
			.sentDate(System.currentTimeMillis())
			.sender(sender)
			.socialNetwork(SocialNetwork.Facebook)
			.externalIdentifier(UUID.randomUUID().toString())
			.owner(owner)
			.socialNetwork(SocialNetwork.Facebook)
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
	
	@Test
	public void testFindByExternalIdentifier() throws Exception {
		Message persisted = messageRepository.getByExternalIdentifierAndSocialNetwork(message.getExternalIdentifier(), owner.getUserId(), SocialNetwork.Facebook);;
		Assert.assertNotNull(persisted);
		Assert.assertTrue(persisted.getMessageId().longValue() == message.getMessageId().longValue());
		
		// query by different id
		persisted = messageRepository.getByExternalIdentifierAndSocialNetwork(UUID.randomUUID().toString(), owner.getUserId(), SocialNetwork.Facebook);;
		Assert.assertNull(persisted);
		
		// query by same id, different network
		persisted = messageRepository.getByExternalIdentifierAndSocialNetwork(message.getExternalIdentifier(), owner.getUserId(), SocialNetwork.Facebook);;
		Assert.assertNotNull(persisted);
				
	}


}
