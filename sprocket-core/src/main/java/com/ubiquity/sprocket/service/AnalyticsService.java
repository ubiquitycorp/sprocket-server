package com.ubiquity.sprocket.service;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.Ordering;
import com.google.common.collect.UnmodifiableIterator;
import com.google.gson.reflect.TypeToken;
import com.niobium.common.serialize.JsonConverter;
import com.niobium.repository.ListCache;
import com.niobium.repository.ListCacheRedisImpl;
import com.ubiquity.sprocket.domain.Event;
import com.ubiquity.sprocket.domain.EventType;

public class AnalyticsService {
	
	private ListCache cache;
	private Type serializeablePropertiesType;
	
	private JsonConverter jsonConverter = JsonConverter.getInstance();
	
	/***
	 * Sets up redis cache interfaces for each event type
	 * 
	 * @param configuration
	 */
	public AnalyticsService(Configuration configuration) {
		cache = new ListCacheRedisImpl(configuration.getInt("redis.tracker.event.database"));
		serializeablePropertiesType = new TypeToken<Map<String, String>>(){}.getType();
	}
	
	/***
	 * Track an event
	 * 
	 * @param event
	 */
	public void track(Event event) {
		cache.put(event.getType().toString().toLowerCase(), jsonConverter.convertToPayload(event.getProperties()));
	}
	
	public void clearAllByType(EventType type) {
		cache.removeEntries(type.toString().toLowerCase());
	}
	
	/***
	 * Returns events by type
	 * 
	 * @param type
	 * @return
	 */
	public List<Event> findAllByType(EventType type) {
		List<Event> events = new LinkedList<Event>();
		List<String> entries = cache.getEntries(type.toString().toLowerCase());
		for(String entry : entries) {
			Map<String, Object> properties = jsonConverter.convert(entry, serializeablePropertiesType);
			Event event = new Event(type);
			event.getProperties().putAll(properties);
			events.add(event);
		}
		return events;
	}
	
	public Map<String, Long> getTopOccurancesOf(EventType type, String key, int max) {
		return getTopOccurancesOf(findAllByType(type), key, max);
	}
	
	private Map<String, Long> getTopOccurancesOf(List<Event> events, String key, int max) {
		
		Map<String, Long> results = new HashMap<String, Long>();
		
		// load into multi-set
		Multiset<Object> values = HashMultiset.create();
		for(Event event : events) {
			values.add(event.getProperties().get(key).toString());
		}
		
		// iteratte over top n
		ImmutableList<Entry<Object>> topOccurances = topByCount(values, max);
		
		UnmodifiableIterator<Entry<Object>> iterator = topOccurances.iterator();
		while(iterator.hasNext()) {
			Entry<Object> entry = iterator.next();
			results.put(entry.getElement().toString(), (long)entry.getCount());
		}		
		return results;
		
	}
	
	private <T> ImmutableList<Entry<T>> sortedByCount(Multiset<T> multiset) {
        Ordering<Multiset.Entry<T>> countComp = new Ordering<Multiset.Entry<T>>() {
            public int compare(Multiset.Entry<T> e1, Multiset.Entry<T> e2) {
                return e2.getCount() - e1.getCount();
            }
        };
        return countComp.immutableSortedCopy(multiset.entrySet());
    }

    private <T> ImmutableList<Entry<T>> topByCount(Multiset<T> multiset, int limit) {
        ImmutableList<Entry<T>> sortedByCount = sortedByCount(multiset);
        if (sortedByCount.size() > limit) {
            sortedByCount = sortedByCount.subList(0, limit);
        }

        return sortedByCount;
    }
}
