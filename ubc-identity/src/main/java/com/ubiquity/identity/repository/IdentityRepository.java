package com.ubiquity.identity.repository;

import com.niobium.repository.Repository;
import com.ubiquity.identity.domain.Identity;

/***
 * 
 * Interface exposing CRUD methods for the identity entity
 * 
 * @author chris
 *
 */
public interface IdentityRepository extends Repository <Long, Identity> {}

