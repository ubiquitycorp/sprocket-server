package com.ubiquity.sprocket.repository;

import java.util.List;

import org.lilyproject.repository.api.FieldNotFoundException;
import org.lilyproject.repository.api.Record;
import org.lilyproject.repository.api.RecordException;
import org.lilyproject.repository.api.Repository;

import com.niobium.repository.BaseRepositoryLilyImpl;
import com.ubiquity.integration.domain.AgeRange;
import com.ubiquity.integration.domain.Gender;
import com.ubiquity.sprocket.domain.Profile;

public class ProfileRepositoryLilyImpl extends BaseRepositoryLilyImpl <Profile> implements ProfileRepository {

	public ProfileRepositoryLilyImpl(String namespace, Repository repository) {
		super(namespace, repository, Profile.class);
	}

	@Override
	public void create(Profile profile) {
		Record record = prepareNewRecord();
		setFieldValue(record, "gender", profile.getGender().ordinal());
		AgeRange ageRange = profile.getAgeRange();
		if(ageRange != null) {
			setFieldValue(record, "min_age", ageRange.getMin());
			setFieldValue(record, "max_age", ageRange.getMax());
		}
		setFieldValue(record, "search_history", profile.getSearchHistory());
		
		record = create(record);
		// set the master id
		profile.setProfileId(record.getId().getMaster().toString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Profile read(String id) {
		Record record = readExistingRecord(id);

		try {
			Profile.Builder profileBuilder = new Profile.Builder()
			.profileId(id)
			.gender(Gender.getGenderById((Integer)record.getField("gender")));
			
			// look for min or max age; if either one is set, then we'll store an age range value
			try {
				Integer minAge = (Integer)record.getField("min_age");
				Integer maxAge = (Integer)record.getField("max_age");
				if(minAge != null || maxAge != null) {
					profileBuilder.ageRage(new AgeRange(minAge, maxAge));
				}
			} catch (FieldNotFoundException e) {}
				

			Profile profile = profileBuilder.build();
			// now add collections
			profile.getSearchHistory().addAll((List<String>)record.getField("search_history"));

			return profile;

		} catch (FieldNotFoundException | RecordException e) {
			throw new RuntimeException("Could not read record", e);
		}
	}



	@Override
	public void update(Profile profile) {
		try {
			Record record = prepareNewRecord(profile.getProfileId());
			setFieldValue(record, "gender", profile.getGender().ordinal());
			AgeRange ageRange = profile.getAgeRange();
			if(ageRange != null) {
				setFieldValue(record, "min_age", ageRange.getMin());
				setFieldValue(record, "max_age", ageRange.getMax());
			}
			setFieldValue(record, "search_history", profile.getSearchHistory());
			update(record);
		} catch (RecordException e) {
			throw new RuntimeException("Could not update record", e);
		}
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
