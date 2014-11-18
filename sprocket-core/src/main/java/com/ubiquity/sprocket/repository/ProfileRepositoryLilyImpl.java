package com.ubiquity.sprocket.repository;

import java.util.LinkedList;
import java.util.List;

import org.lilyproject.repository.api.FieldNotFoundException;
import org.lilyproject.repository.api.Record;
import org.lilyproject.repository.api.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.BaseRepositoryLilyImpl;
import com.ubiquity.integration.domain.AgeRange;
import com.ubiquity.integration.domain.Gender;
import com.ubiquity.sprocket.domain.Profile;

public class ProfileRepositoryLilyImpl extends BaseRepositoryLilyImpl <Profile> implements ProfileRepository {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	public ProfileRepositoryLilyImpl(String namespace, Repository repository) {
		super(namespace, repository, Profile.class);
	}

	@Override
	public void create(Profile profile) {
		Record record = prepareNewRecord(profile.getUser().getUserId().toString());
		loadProfileRecord(record, profile);
		loadIdentities(record, profile);

		record = create(record);
		// set the master id
		profile.setProfileId(record.getId().getMaster().toString());

	}

	private String generateIdentityId(Profile profile) {
		return profile.getUser().getUserId().toString() + "-" + String.valueOf(profile.getExternalNetwork()) + "-" + profile.getExternalIdentifier();
	}

	private void loadIdentities(Record record, Profile profile) {
		// now create contact records, if they exist
		List<Profile> identities = profile.getIdentities();
		
		if(!identities.isEmpty()) {
			List<Record> records = new LinkedList<Record>();
			for(Profile identity : identities) {
				Record identityRecord = prepareNewRecord(generateIdentityId(identity), "identity");
				loadProfileRecord(identityRecord, identity);
				records.add(identityRecord);
			}
			log.info("loaded identities {}", records);
			
			setFieldValue(record, "identities", records);
		}
	}
	private void loadProfileRecord(Record record, Profile profile) {
		Gender gender = profile.getGender();
		if(profile.getGender() != null)
			setFieldValue(record, "gender", gender.ordinal());

		AgeRange ageRange = profile.getAgeRange();
		if(ageRange != null) {
			setFieldValue(record, "min_age", ageRange.getMin());
			setFieldValue(record, "max_age", ageRange.getMax());
		}
		setFieldValue(record, "search_history", profile.getSearchHistory());
	}

	@Override
	public Profile read(String id) {
		Record record = readExistingRecord(id);
		return assemble(id, record);
	}

	@SuppressWarnings("unchecked")
	public Profile assemble(String id, Record record) {
		Profile.Builder profileBuilder = new Profile.Builder()
		.profileId(id)
		.gender(Gender.getGenderById((Integer)getFieldValue(record, "gender")));

		// look for min or max age; if either one is set, then we'll store an age range value
		try {
			Integer minAge = (Integer)getFieldValue(record, "min_age");
			Integer maxAge = (Integer)getFieldValue(record, "max_age");
			if(minAge != null || maxAge != null) {
				profileBuilder.ageRange(new AgeRange(minAge, maxAge));
			}
		} catch (FieldNotFoundException e) {}


		Profile profile = profileBuilder.build();
		// now add collections
		profile.getSearchHistory().addAll((List<String>)getFieldValue(record, "search_history"));

		List<Record> identityRecords = (List<Record>)getFieldValue(record, "identities");
		if(identityRecords != null && !identityRecords.isEmpty()) {
			if(!identityRecords.isEmpty()) {
				for(Record identityRecord : identityRecords) {
					log.info("fields {}", identityRecord.getId());
					Profile identity = assemble(null, identityRecord);
					profile.getIdentities().add(identity);
				}
			}
		}
		return profile;
	}

	

	@Override
	public void update(Profile profile) {
		Record record = prepareExistingRecord(profile.getProfileId());
		loadProfileRecord(record, profile);
		loadIdentities(record, profile);

		update(record);
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
