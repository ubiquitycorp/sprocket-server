package com.ubiquity.social.api.gmail;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;

import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.ubiquity.social.api.gmail.endpoints.GmailApiEndpoints;
import com.ubiquity.social.api.util.NamespaceFilter;
import com.ubiquity.social.domain.ExternalIdentity;
import com.ubiquity.social.domain.Message;


public class GmailAPI {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	

	private GmailApiEndpoints gmailApiEndpoints;
	
	public GmailAPI() {
		// this initialization only needs to be done once per VM (TODO: find the right place to put this)
		RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
		gmailApiEndpoints = ProxyFactory.create(GmailApiEndpoints.class, "https://mail.google.com/");
	}
	
	public List<Message> findMessages(ExternalIdentity externalIdentity) {
		List<Message> messages = new LinkedList<Message>();
		ClientResponse<String> response = null;
		try {
			response = gmailApiEndpoints.getFeed(" Bearer "+ externalIdentity.getAccessToken());
			if(response.getResponseStatus().getStatusCode() != 200)
				return messages;
			 // create JAXB context and instantiate marshaller
	
				JAXBContext context = JAXBContext.newInstance(Feed.class);
			    Unmarshaller unmarshaller = context.createUnmarshaller();
			    
			  //Create an XMLReader to use with our filter
			    XMLReader reader = XMLReaderFactory.createXMLReader();

			    //Create the filter (to add namespace) and set the xmlReader as its parent.
			    NamespaceFilter inFilter = new NamespaceFilter("http://www.w3.org/2005/Atom", true);
			    inFilter.setParent(reader);

			    //Prepare the input, in this case a java.io.File (output)
			    InputSource is = new InputSource(new StringReader(response.getEntity()));

			    //Create a SAXSource specifying the filter
			    SAXSource source = new SAXSource(inFilter, is);

			    //Do unmarshalling
			    Object inflated = unmarshaller.unmarshal(source);
			    
			    
			    Feed feed = (Feed)inflated;
			    for(Entry entry : feed.getEntries()) {
			    	String title = entry.getTitle();
			    	String body = entry.getSummary();
			    	
			    	Message message = new Message.Builder().title(title).title(body).build();
			    	messages.add(message);
			    	log.debug("entry title: " + entry.getTitle());
			    }
			    
			
		} catch (SAXException e) {
			throw new RuntimeException("Unable to parse response from Gmail", e);
		} catch (JAXBException e) {
			throw new RuntimeException("Unable to parse response from Gmail", e);
		}  finally {
			if(response != null)
				response.releaseConnection();
		}
		return messages;
	}

}
