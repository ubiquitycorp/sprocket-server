package com.ubiquity.social.api.gmail;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;
import org.jboss.resteasy.plugins.providers.atom.Person;

import com.ubiquity.identity.domain.User;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Message;

public class GmailApiDtoAssembler {

	public static List<Message> assemble(javax.mail.Message[] messages,
			User owner) throws MessagingException, IOException {
		List<Message> messagesresult = new LinkedList<Message>();
		// map messages to DB messages
		for (int i = 0; i < messages.length; i++) {

			Object body = messages[0].getContent();

			String textbody = "";
			if (messages[0].isMimeType("text/plain")) {
				textbody = body.toString();
			}
			InternetAddress fromAddress = (InternetAddress) messages[0]
					.getFrom()[0];

			Message message = new Message.Builder()
					.title(messages[0].getSubject())
					.body(textbody)
					.messageId(1l)
					.owner(owner)
					.sender(new Contact.Builder()
							.email((messages[0].getFrom()[0].toString()))
							.displayName(fromAddress.getPersonal()).build())
					.build();
			messagesresult.add(message);

		}

		return messagesresult;
	}

	public static List<Message> assemble(Feed feed, User owner) {

		List<Message> messages = new LinkedList<Message>();

		for (Entry entry : feed.getEntries()) {
			String title = entry.getTitle();
			String body = entry.getSummary();

			Person person = entry.getAuthors().get(0);
			Message message = new Message.Builder()
					.title(title)
					.body(body)
					.messageId(1l)
					.owner(owner)
					.sender(new Contact.Builder().email(person.getEmail())
							.displayName(person.getName()).build()).build();
			messages.add(message);
		}

		return messages;
	}

}
