package com.ubiquity.sprocket.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.niobium.repository.BaseRepositoryJpaImpl;
import com.ubiquity.sprocket.domain.Configuration;
import com.ubiquity.sprocket.domain.ConfigurationType;

public class ConfigurationRepositoryJpaImpl extends BaseRepositoryJpaImpl<Long, Configuration> implements
ConfigurationRepository {

	public ConfigurationRepositoryJpaImpl(EntityManager em) {
		super(Configuration.class, em);
	}

	public ConfigurationRepositoryJpaImpl() {
		super(Configuration.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Configuration> findConfigurationByType(ConfigurationType type) {
		Query query = getEntityManager().createQuery("select config from Configuration config where config.configurationType = :type and isActive = 1 order by network,name");
		query.setParameter("type", type);
		return (List<Configuration>)query.getResultList();
	}
}
