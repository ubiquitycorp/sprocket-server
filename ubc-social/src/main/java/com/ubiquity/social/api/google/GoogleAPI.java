package com.ubiquity.social.api.google;

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
import org.jboss.resteasy.plugins.providers.atom.Feed;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.social.api.SocialAPI;
import com.ubiquity.social.api.exception.AuthorizationException;
import com.ubiquity.social.api.gmail.GmailApiDtoAssembler;
import com.ubiquity.social.api.gmail.endpoints.GmailApiEndpoints;
import com.ubiquity.social.api.google.dto.GooglePlusApiDtoAssembler;
import com.ubiquity.social.api.google.dto.container.GoogleItemsDto;
import com.ubiquity.social.api.google.dto.container.GoogleRequestFailureDto;
import com.ubiquity.social.api.google.dto.model.GooglePersonDto;
import com.ubiquity.social.api.google.endpoints.GooglePlusApiEndpoints;
import com.ubiquity.social.api.util.NamespaceFilter;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Event;
import com.ubiquity.social.domain.ExternalIdentity;
import com.ubiquity.social.domain.Message;

/***
 * Google API class
 * 
 * @author Peter
 * 
 */

public class GoogleAPI implements SocialAPI {

	private static SocialAPI google = null;
	OAuthService service = null;
	
	@SuppressWarnings("unused")
	private Logger log = LoggerFactory.getLogger(getClass());

	private GooglePlusApiEndpoints googleApi;
	private GmailApiEndpoints gmailApiEndpoints;

	private static JAXBContext context;

	private static JAXBContext getJAXBContext() throws JAXBException {
		if(context == null)
			context = JAXBContext.newInstance(Feed.class);
		return context;
	}
	
	private JsonConverter jsonConverter = JsonConverter.getInstance();
	
	private GoogleAPI() {
		// this initialization only needs to be done once per VM
		RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
		googleApi = ProxyFactory.create(GooglePlusApiEndpoints.class, "https://www.googleapis.com/plus/v1");
		gmailApiEndpoints = ProxyFactory.create(GmailApiEndpoints.class, "https://mail.google.com/");
	}

	public static SocialAPI getProviderAPI() {
		if (google == null)
			google = new GoogleAPI();
		return google;
	}

	@Override
	public Contact authenticateUser(ExternalIdentity identity) {
		ClientResponse<String> response = null;
		try {
			response = googleApi.getMe(identity.getAccessToken());
			checkError(response);
			
			GooglePersonDto result = jsonConverter.parse(response.getEntity(), GooglePersonDto.class);
			Contact contact = GooglePlusApiDtoAssembler.assembleContact(identity, result);
			return contact;
		} finally {
			if(response != null)
				response.releaseConnection();
		}
	}

	@Override
	public List<Contact> findContactsByOwnerIdentity(ExternalIdentity identity) {
		List<Contact> contacts = new LinkedList<Contact>();
		ClientResponse<String> response = null;
		try {
			response = googleApi.getFriends(identity.getAccessToken());
			checkError(response);

			GoogleItemsDto result = jsonConverter.parse(response.getEntity(), GoogleItemsDto.class);
			
			List<GooglePersonDto> peopleDtoList = jsonConverter.convertToListFromList(result.getItems(), GooglePersonDto.class);
			for(GooglePersonDto personDto : peopleDtoList) {
				Contact contact = GooglePlusApiDtoAssembler.assembleContact(identity.getUser(), personDto);
				contacts.add(contact);
			}
			return contacts;
		} finally {
			if(response != null)
				response.releaseConnection();
		}
	}

	@Override
	public List<Event> findEventsCreatedByContacts(ExternalIdentity identity,
			List<Contact> contacts) {
		throw new UnsupportedOperationException();

	}

	@Override
	public Boolean postToWall(ExternalIdentity fromIdentity,
			ExternalIdentity toIdentity, String message) {
		throw new UnsupportedOperationException();
	}
	
	private String getErrorMessage(ClientResponse<String> response) {
		String errorMessage = null;
		String errorBody = response.getEntity();
		if(errorBody != null) {
			GoogleRequestFailureDto failure = jsonConverter.parse(errorBody, GoogleRequestFailureDto.class);
			errorMessage = failure.getError().getMessage();
		} else {
			errorMessage = "Unable to authenticate with provided credentials";
		}
		return errorMessage;
	}
	
	private void checkError(ClientResponse<String> response) {
		int statusCode = response.getResponseStatus().getStatusCode();
		if (statusCode != 200) {
			if(statusCode == 401 || statusCode == 403)
				throw new AuthorizationException(getErrorMessage(response));
			else
				throw new RuntimeException(getErrorMessage(response));
		}
	}

	@Override
	public List<Message> listMessages(ExternalIdentity externalIdentity) {
		ClientResponse<String> response = null;
		try {
			response = gmailApiEndpoints.getFeed(" Bearer "+ externalIdentity.getAccessToken());
			if(response.getResponseStatus().getStatusCode() != 200) {
				log.error("Error reading gmail: " + response.getEntity());
				throw new RuntimeException("Unable to process gmail messages");
			}
				
			 // create JAXB context and instantiate marshaller
	
				
			    Unmarshaller unmarshaller = getJAXBContext().createUnmarshaller();
			    
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
			    
			    return GmailApiDtoAssembler.assemble((Feed)inflated);
			   
			
		} catch (SAXException e) {
			throw new RuntimeException("Unable to parse response from Gmail", e);
		} catch (JAXBException e) {
			throw new RuntimeException("Unable to parse response from Gmail", e);
		}  finally {
			if(response != null)
				response.releaseConnection();
		}
	}

	@Override
	public List<Activity> listActivities(ExternalIdentity external) {
		throw new UnsupportedOperationException();
	}
}
