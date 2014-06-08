package com.ubiquity.commerce.repository;

import java.util.List;

import com.niobium.repository.Repository;
import com.ubiquity.commerce.domain.ItemDoubleOption;
import com.ubiquity.commerce.domain.ItemOption;
import com.ubiquity.commerce.domain.ItemStringOption;

/***
 * 
 * @author chris
 *
 */
public interface ItemOptionRepository extends Repository <Long, ItemOption> {
	List<ItemDoubleOption> findByValues(Double[] values);
	List<ItemStringOption> findByValues(String[] values);
}
