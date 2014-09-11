package com.ubiquity.sprocket.location;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderGeometry;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.code.geocoder.model.LatLng;
import com.google.code.geocoder.model.LatLngBounds;
import com.ubiquity.location.domain.Geobox;
import com.ubiquity.location.domain.Location;

public class LocationConverter {
	
	private static LocationConverter converterInstance;

	public static synchronized LocationConverter getInstance() {
		if(converterInstance == null)
			converterInstance = new LocationConverter();
		return converterInstance;
	}
	
	/***
	 * Returns location (lat / lon pair) for a location description
	 * 
	 * @param description (i.e., Los Angeles)
	 * 
	 * @return list of geobox coordinates of potential matches or empty if it can't be determined
	 * 
	 * @throws IOException
	 */
	public List<Geobox> convertFromLocationDescription(String description, String language) throws IOException {
		
		List<Geobox> geoboxes = new LinkedList<Geobox>();
		
		final Geocoder geocoder = new Geocoder();
		GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(description).setLanguage(language).getGeocoderRequest();
		GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);
		List<GeocoderResult> results = geocoderResponse.getResults();
		
		for(GeocoderResult result : results) {
			GeocoderGeometry geometry = result.getGeometry();
			
			LatLng latLon = geometry.getLocation();
			LatLngBounds bounds = geometry.getViewport();

			Location center = new Location.Builder().latitude(latLon.getLat()).longitude(latLon.getLng()).build();
			Location lowerLeft = new Location.Builder().latitude(bounds.getSouthwest().getLat()).longitude(bounds.getSouthwest().getLng()).build();
			Location upperRight = new Location.Builder().latitude(bounds.getNortheast().getLat()).longitude(bounds.getNortheast().getLng()).build();

			geoboxes.add(new Geobox.Builder().center(center).lowerLeft(lowerLeft).upperRight(upperRight).build());
			
			
		}
		return geoboxes;
	}

}
