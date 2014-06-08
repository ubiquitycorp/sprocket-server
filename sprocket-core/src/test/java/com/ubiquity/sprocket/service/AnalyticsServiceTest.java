package com.ubiquity.sprocket.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.sprocket.domain.Event;
import com.ubiquity.sprocket.domain.EventType;


public class AnalyticsServiceTest {

	private static AnalyticsService analyticsService;
	private static Event event;

	private Logger log = LoggerFactory.getLogger(getClass());
	
	@BeforeClass
	public static void setUp() throws Exception {
		Configuration config = new PropertiesConfiguration("test.properties");
		analyticsService = new AnalyticsService(config);	
		
		// clear db
		analyticsService.clearAllByType(EventType.Search);
		analyticsService.clearAllByType(EventType.UserAddedIdentity);

		
		event = new Event(EventType.Search);
		event.getProperties().put("term", UUID.randomUUID());
	}

	@Test
	public void testBasicTrack() {
		analyticsService.track(event);
		
		
		String expected = event.getProperties().get("term").toString();
		
		List<Event> trackedEvents = analyticsService.findAllByType(EventType.Search);
		Boolean isFound = Boolean.FALSE;
		for(Event tracked : trackedEvents) {
			log.debug("tracked {}", tracked);
			
			String trackedTerm = tracked.getProperties().get("term").toString();
			if(expected.equals(trackedTerm)) {
				isFound = Boolean.TRUE;
				break;
			}
		}
		Assert.assertTrue(isFound);
		
		
	}
	
	@Test
	public void testGetTopOccurances() {
		
		// add 3, and then add 2 random + 1 more than the max
		String topTerm = "fuck";
		event = new Event(EventType.Search);
		event.getProperties().put("term", topTerm);
		analyticsService.track(event);

		event = new Event(EventType.Search);
		event.getProperties().put("term", topTerm);
		analyticsService.track(event);

		event = new Event(EventType.Search);
		event.getProperties().put("term", topTerm);
		analyticsService.track(event);

		event = new Event(EventType.Search);
		event.getProperties().put("term", UUID.randomUUID().toString());
		analyticsService.track(event);

		event = new Event(EventType.Search);
		event.getProperties().put("term", UUID.randomUUID().toString());
		analyticsService.track(event);

		event = new Event(EventType.Search);
		event.getProperties().put("term", UUID.randomUUID().toString());
		analyticsService.track(event);

		Map<String, Long> occurances = analyticsService.getTopOccurancesOf(EventType.Search, "term", 5);
		
		// make sure we respect limit
		Assert.assertTrue(occurances.size() == 5);
		
		// should be 3
		Assert.assertEquals(3l, occurances.get(topTerm).longValue());
		
		// the others should all be 1
		for(String term : occurances.keySet()) {
			if(!term.equals(topTerm))
				Assert.assertEquals(1l, occurances.get(term).longValue());
		}
		
		
	}

}
