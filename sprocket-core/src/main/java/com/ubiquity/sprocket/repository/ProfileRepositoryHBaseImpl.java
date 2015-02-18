package com.ubiquity.sprocket.repository;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.integration.domain.AgeRange;
import com.ubiquity.integration.domain.Gender;
import com.ubiquity.sprocket.domain.Profile;
import com.ubiquity.sprocket.domain.ProfilePK;

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
			Put put = new Put(Bytes.toBytes(profile.getProfileId().toString()));
			loadProfileIntoPut(put, profile);
			put(put);

			// if we have identities, store them
			List<Profile> identities = profile.getIdentities();
			for(Profile identity : identities) {
				put = new Put(Bytes.toBytes(identity.getProfileId().toString()));
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

	}

	@Override
	public Profile read(ProfilePK profileId) {

		try {
			Get get = new Get(Bytes.toBytes(profileId.toString()));
			get.addFamily(Bytes.toBytes(HBaseSchema.ColumnFamilies.ATTRIBUTES));			
			return assembleProfileFromGet(profileId, get);
			
		} finally {
			close();
		}
	}

	/**
	 * Assembles a profile from a profile pk
	 * 
	 * @param id
	 * @param get
	 * @return
	 */
	private Profile assembleProfileFromGet(ProfilePK id, Get get) {
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
		return profile;
	}

	@Override
	public void delete(Profile profile) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addToSearchHistory(ProfilePK pk, String searchTerm) {
		String qualifier = HBaseSchema.Qualifiers.PREFIX_SEARCH_TERM + "-" + searchTerm;
		try {
			incrementValue(pk.toString(), HBaseSchema.ColumnFamilies.HISTORY, qualifier, 1l);
		} finally {
			close();
		}
	}

	@Override
	public List<String> findMostSearchedOn(ProfilePK pk, Integer n) {

		String[] mostSearchedOn = new String[n];
		Long[] highestValues = new Long[n];
		Arrays.fill(highestValues, 0l);
		
		Scan scan = createScanWithPrefixFilter(pk.toString(), HBaseSchema.ColumnFamilies.HISTORY, HBaseSchema.Qualifiers.PREFIX_SEARCH_TERM);
		try {
			ResultScanner rs = getTable().getScanner(scan);
			for (Result r = rs.next(); r != null; r = rs.next()) {
				Long count = new Long(0); // start counter for this profile
				for (Cell cell : r.rawCells()) {
					count += new BigInteger(cell.getValueArray()).longValue(); // count the number of times something was hig
					for(int i = 0; i < highestValues.length; i++) {
						if(count > highestValues[i]) {
							highestValues[i] = count;
							mostSearchedOn[i] = new String(CellUtil.cloneQualifier(cell)).split("-", 2)[1];
							count = new Long(0);
							break;
						}
					}
				}
			}
			rs.close();

		} catch (IOException e) {
			throw new RuntimeException("Could not scan row", e);
		}
		
		List<String> results = new LinkedList<String>();
		for(int i = 0; i < mostSearchedOn.length;i++) {
			String searchTerm = mostSearchedOn[i];
			if(searchTerm != null)
				results.add(searchTerm);
		}
		return results;
		
	}
	
	

}
