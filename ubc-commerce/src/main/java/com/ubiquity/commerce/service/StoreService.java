package com.ubiquity.commerce.service;

import java.util.List;

import org.apache.commons.configuration.Configuration;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.commerce.domain.Store;
import com.ubiquity.commerce.repository.StoreRepository;
import com.ubiquity.commerce.repository.StoreRepositoryJpaImpl;

/***
 * 
 * @author peter.tadros
 * 
 */
public class StoreService {

	private StoreRepository storeRepository;

	/***
	 * Parameterized constructor builds a manager with required configuration
	 * property
	 * 
	 * @param configuration
	 */
	public StoreService(Configuration configuration) {
		storeRepository = new StoreRepositoryJpaImpl();
	}

	/***
	 * Returns the full list of stores
	 * 
	 * @return
	 */
	public List<Store> findAllStores() {
		try {
			return storeRepository.findAll();
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

	/***
	 * Creates a store in the underlying database.
	 * 
	 * @param store
	 */
	public void create(Store store) {
		try {				
			EntityManagerSupport.beginTransaction();
			storeRepository.create(store);
			EntityManagerSupport.commit();
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}
	
	/***
	 * Updates a store in the underlying database.
	 * 
	 * @param store
	 */
	public void update(Store store) {
		try {				
			EntityManagerSupport.beginTransaction();
			storeRepository.update(store);
			EntityManagerSupport.commit();
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}
}
