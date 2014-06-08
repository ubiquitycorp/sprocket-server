package com.ubiquity.commerce.service;

import java.util.List;

import org.apache.commons.configuration.Configuration;

import com.niobium.repository.CollectionVariant;
import com.niobium.repository.cache.DataCacheKeys;
import com.niobium.repository.cache.DataModificationCache;
import com.niobium.repository.cache.DataModificationCacheRedisImpl;
import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.commerce.domain.Item;
import com.ubiquity.commerce.domain.ItemDoubleOption;
import com.ubiquity.commerce.domain.ItemOption;
import com.ubiquity.commerce.domain.ItemOptionType;
import com.ubiquity.commerce.repository.ItemOptionRepository;
import com.ubiquity.commerce.repository.ItemOptionRepositoryJpaImpl;
import com.ubiquity.commerce.repository.ItemRepository;
import com.ubiquity.commerce.repository.ItemRepositoryJpaImpl;
import com.ubiquity.commerce.repository.cache.CommerceCacheKeys;

/***
 * 
 * @author peter.tadros
 * 
 */
public class ItemService {

	private DataModificationCache dataModificationCache;
	private ItemRepository itemRepository;
	private ItemOptionRepository itemOptionRepository;

	/***
	 * Parameterized constructor builds a manager with required configuration
	 * property
	 * 
	 * @param configuration
	 */
	public ItemService(Configuration configuration) {
		dataModificationCache = new DataModificationCacheRedisImpl(
				configuration.getInt(DataCacheKeys.Databases.ENDPOINT_MODIFICATION_DATABASE_GENERAL));
		itemRepository = new ItemRepositoryJpaImpl();
		itemOptionRepository = new ItemOptionRepositoryJpaImpl();
	}

	/***
	 * Returns the full list of items
	 * 
	 * @param userId
	 * @param ifModifiedBy
	 * @return
	 */
	public CollectionVariant<Item> findAllItems(Long ifModifiedBy) {

		Long lastModified = dataModificationCache.getLastModified(CommerceCacheKeys.Keys.ITEMS, ifModifiedBy);

		// If there is no cache entry, there are no items
		if (lastModified == null)
			return null;
		try {
			List<Item> items = itemRepository.findAll();
			return new CollectionVariant<Item>(items, lastModified);
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

	/***
	 * Returns item options that match the input values.
	 * 
	 * @param values
	 * @return
	 */
	public List<ItemDoubleOption> findByValues(Double[] values) {
		try {
			return itemOptionRepository.findByValues(values);
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

	/***
	 * Creates an item in the underlying database.
	 * 
	 * @param item
	 */
	public void create(Item item, String optionsKey, Double[] values) {
		try {
			
			List<ItemDoubleOption> options = itemOptionRepository.findByValues(values);
			if(options.size() != values.length)
				throw new IllegalArgumentException("The number of values and entries in the db must match");
			
			ItemOptionType optionType = new ItemOptionType(item, optionsKey);
			optionType.getOptions().addAll(options);
			item.getOptions().put(optionsKey, optionType);

			// start transaction
			EntityManagerSupport.beginTransaction();
			itemRepository.create(item);
			// commit it
			EntityManagerSupport.commit();

		} finally {
			EntityManagerSupport.closeEntityManager();
		}

		// Update last modified cache
		dataModificationCache.setLastModified(CommerceCacheKeys.Keys.ITEMS, System.currentTimeMillis());

	}

	/**
	 * Creates an item option in the underlying database.
	 * 
	 * @param option
	 */
	public void create(ItemOption option) {
		try {
			EntityManagerSupport.beginTransaction();
			itemOptionRepository.create(option);
			EntityManagerSupport.commit();

		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

	public Item getByItemId(Long itemId) {
		try {
			return itemRepository.read(itemId);
		} finally {
			EntityManagerSupport.closeEntityManager();
		}

	}

}
