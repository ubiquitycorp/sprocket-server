package com.ubiquity.sprocket.service;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailService {
	
	private String username;
	private String password;
	private String host;
	private String port;
	private String from;
	private String templateData;
	private Session session;
	private Logger log = LoggerFactory.getLogger(getClass());
	
	public EmailService(Configuration configuration) {
		username = configuration.getString("mail.username");
		password = configuration.getString("mail.password");
		host = configuration.getString("mail.host");
		port = configuration.getString("mail.port");
		from = configuration.getString("mail.from");
	}
	/***
	 * 
	 * Sends a mail to the recipient with the arguments that fill in the placeholder values
	 * in the template
	 * 
	 * @param to
	 * @param message
	 * @throws Exception 
	 */
	@SuppressWarnings("static-access")
	public void sendUsingTemplate(String to, String subject, Object[] arguments, String templateFileName) {
		try {
			//
			// Create an email message
			//
			Multipart multipart = new MimeMultipart("alternative");
			Message message = new MimeMessage(getSession());
			message.setSentDate(new java.util.Date());
			message.setFrom(new InternetAddress(from));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject(subject);
			MimeBodyPart htmlPart = new MimeBodyPart();
			String htmlContent = MessageFormat.format(getTemplateData(templateFileName), arguments);
			htmlPart.setContent(htmlContent, "text/html");
			multipart.addBodyPart(htmlPart);
			message.setContent(multipart);
			//
			// Send a message
			//
			Transport transport = session.getTransport("smtp");
			transport.send(message);
		} catch (MessagingException ex) {
			log.error("Unable to send mail to recipient " + ex.getMessage());
			throw new RuntimeException("Unable to send mail to recipient", ex);
		} catch (Exception ex){
			log.error("Unexpected error, unable to send mail to recipient " + ex.getMessage());
		}
	}

	/**
	 * Retrieves the template content from the text file in the resource path defined by the "template"
	 * property
	 * 
	 * @return
	 */
	private String getTemplateData(String templateFileName) {
		if(templateData == null) {
			// open the template file and save it,
			try {
				templateData = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(templateFileName));
			} catch (IOException e) {
				throw new IllegalArgumentException("Error reading template file " + templateFileName);
			}
		}
		return templateData;
	}
	/***
	 * Returns a mail session or creates one if it does not exist
	 * @return
	 */
	private Session getSession() {
		if (session == null) {
			Properties properties = new Properties();
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.socketFactory.port", port);
			properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			//properties.put("mail.debug", "true");
			if(username == null || password == null) {
				properties.put("mail.smtp.auth", "false");
				session = Session.getInstance(properties);
			} else {
				properties.put("mail.smtp.auth", "true");
				session = Session.getDefaultInstance(properties, new Authenticator() {
					@Override
					protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
						return new javax.mail.PasswordAuthentication(username, password);
					}
				});
			}
		}
		return session;
	}
}
