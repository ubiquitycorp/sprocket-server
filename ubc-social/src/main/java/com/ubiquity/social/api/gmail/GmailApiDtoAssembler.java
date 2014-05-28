package com.ubiquity.social.api.gmail;

import java.util.LinkedList;
import java.util.List;

import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;
import org.jboss.resteasy.plugins.providers.atom.Person;

import com.ubiquity.identity.domain.User;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Message;

public class GmailApiDtoAssembler {


	public static List<Message> assemble(Feed feed, User owner) {

		List<Message> messages = new LinkedList<Message>();
		
		for(Entry entry : feed.getEntries()) {
			String title = entry.getTitle();
			String body = entry.getSummary();
	
			
			Person person = entry.getAuthors().get(0);
			Message message = new Message.Builder()
					.title(title)
					.body(body)
					.messageId(1l)
					.owner(owner)
					.sender(new Contact.Builder()
						.email(person.getEmail())
						.displayName(person.getName())
						.build())
					.build();
			messages.add(message);
		}
		
		return messages;
	}

}
