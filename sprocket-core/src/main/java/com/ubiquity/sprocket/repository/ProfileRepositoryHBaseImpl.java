package com.ubiquity.sprocket.repository;

import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.integration.domain.AgeRange;
import com.ubiquity.integration.domain.Gender;
import com.ubiquity.sprocket.domain.Profile;

public class ProfileRepositoryHBaseImpl extends BaseRepositoryHBaseImpl <Profile> implements ProfileRepository {

	@SuppressWarnings("unused")
	private Logger log = LoggerFactory.getLogger(getClass());

	/***
	 * Creates a repo with the name space and repository
	 * 
	 * @param namespace
	 * @param repository
	 */
	public ProfileRepositoryHBaseImpl() {
		super(Profile.class);
	}

	@Override
	public void create(Profile profile) {
		try {
			Put put = new Put(Bytes.toBytes(profile.getProfileId()));
			loadProfileIntoPut(put, profile);
			put(put);

			// if we have identities, store them
			List<Profile> identities = profile.getIdentities();
			for(Profile identity : identities) {
				put = new Put(Bytes.toBytes(identity.getProfileId()));
				loadProfileIntoPut(put, identity);
				put(put);
			}
		} finally {
			close();
		}
	}

	@Override
	public void update(Profile profile) {
		create(profile);
	}

	private void loadProfileIntoPut(Put put, Profile profile) {
		// add gender if we have it
		if(profile.getGender() != null)
			addValue(put, HBaseSchema.ColumnFamilies.ATTRIBUTES, HBaseSchema.Qualifiers.GENDER, profile.getGender().ordinal());

		// add age range if we have it
		if(profile.getAgeRange() != null) {
			AgeRange ageRange = profile.getAgeRange();
			if(ageRange.getMin() != null)
				addValue(put, HBaseSchema.ColumnFamilies.ATTRIBUTES, HBaseSchema.Qualifiers.MIN_AGE, ageRange.getMin());
			if(ageRange.getMax() != null)
				addValue(put, HBaseSchema.ColumnFamilies.ATTRIBUTES, HBaseSchema.Qualifiers.MAX_AGE, ageRange.getMax());
		}
		
		if(profile.getGroupMembership() != null)
			addValue(put, HBaseSchema.ColumnFamilies.ATTRIBUTES, HBaseSchema.Qualifiers.GROUP_MEMBERSHIP, profile.getGroupMembership());

		addValue(put, HBaseSchema.ColumnFamilies.HISTORY, HBaseSchema.Qualifiers.SEARCH, profile.getSearchHistory());
	}

	@Override
	public Profile read(String id) {

		try {
			Get get = new Get(Bytes.toBytes(id));
			get.addFamily(Bytes.toBytes(HBaseSchema.ColumnFamilies.ATTRIBUTES));
			get.addFamily(Bytes.toBytes(HBaseSchema.ColumnFamilies.HISTORY));
			
			return assembleProfileFromGet(id, get);
			
		} finally {
			close();
		}
	}

	private Profile assembleProfileFromGet(String id, Get get) {
		// execute query and get result back
		Result result = getResult(get);

		// start inflating results into a profile
		Profile.Builder profileBuilder = new Profile.Builder().profileId(id);

		// set gender if we got it
		Integer ordinal = getIntegerValue(result, HBaseSchema.ColumnFamilies.ATTRIBUTES, HBaseSchema.Qualifiers.GENDER);
		if(ordinal != null) {
			profileBuilder.gender(Gender.getGenderById(ordinal));
		} 

		// age range
		Integer minAge = getIntegerValue(result, HBaseSchema.ColumnFamilies.ATTRIBUTES, HBaseSchema.Qualifiers.MIN_AGE);
		Integer maxAge = getIntegerValue(result, HBaseSchema.ColumnFamilies.ATTRIBUTES, HBaseSchema.Qualifiers.MAX_AGE);
		if(minAge != null || maxAge != null) {
			profileBuilder.ageRange(new AgeRange(minAge, maxAge));
		}

		// groups
		String groupMembership = getStringValue(result, HBaseSchema.ColumnFamilies.ATTRIBUTES, HBaseSchema.Qualifiers.GROUP_MEMBERSHIP);
		profileBuilder.groupMembership(groupMembership);
		
		Profile profile = profileBuilder.build();

		// now do collections
		String[] searchHistory = getStringValues(result, HBaseSchema.ColumnFamilies.HISTORY, HBaseSchema.Qualifiers.SEARCH);
		if(searchHistory != null)
			profile.getSearchHistory().addAll(Arrays.asList(searchHistory));
		
		return profile;
	}

	@Override
	public void delete(Profile profile) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Profile updateAndSelect(Profile obj) {
		throw new UnsupportedOperationException();
	}

}
