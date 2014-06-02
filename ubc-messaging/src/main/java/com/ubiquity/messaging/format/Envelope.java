package com.ubiquity.messaging.format;


public class Envelope {
	
	private DestinationType destination;
	private String identifier;
	private Message body;
	
	
	/***
	 * Parameterized constructor creates a message with required properties
	 * 
	 * @param destination
	 * @param identifier
	 * @param content
	 */
	public Envelope(DestinationType destination, String identifier, Message body) {
		this.destination = destination;
		this.identifier = identifier;
		this.body = body;
	}
	
	public DestinationType getDestination() {
		return destination;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public Message getBody() {
		return body;
	}

}
