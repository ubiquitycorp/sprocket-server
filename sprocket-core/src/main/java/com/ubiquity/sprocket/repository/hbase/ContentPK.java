package com.ubiquity.sprocket.repository.hbase;

import java.io.Serializable;

import com.ubiquity.integration.domain.ExternalNetwork;

public class ContentPK implements Serializable {
	
	private static final long serialVersionUID = -7198418161983794398L;

	private static final String INTERNAL_NETWORK_NAME = "sprocket";

	private ExternalNetwork externalNetwork;
	private String identifier;
	
	public ContentPK(ExternalNetwork externalNetwork, String identifier) {
		this.externalNetwork = externalNetwork;
		this.identifier = identifier;
	}
	
	public ContentPK(String identifier) {
		this.identifier = identifier;
	}
	
	public ExternalNetwork getExternalNetwork() {
		return externalNetwork;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	@Override
	public String toString() {
		return externalNetwork == null ? INTERNAL_NETWORK_NAME + "-" + identifier : externalNetwork.name() + "-" + identifier;
	}
	
	public static ContentPK fromString(String pk) {
		String[] tokens = pk.split("-", 2); // take the first, the rest is the content
		String network = tokens[0];
		String identifier = tokens[1];
		ExternalNetwork externalNetwork = network.equals(INTERNAL_NETWORK_NAME) ? null : ExternalNetwork.valueOfIgnoreCase(network);
		
		return new ContentPK(externalNetwork, identifier);
		
	}
	
	
	
	
	
}