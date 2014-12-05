package com.ubiquity.sprocket.repository;


import java.util.List;
import java.util.UUID;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.factory.TestUserFactory;
import com.ubiquity.integration.domain.Activity;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.VideoContent;
import com.ubiquity.integration.factory.TestActivityFactory;
import com.ubiquity.integration.factory.TestPlaceFactory;
import com.ubiquity.integration.factory.TestVideoContentFactory;
import com.ubiquity.location.domain.Place;
import com.ubiquity.sprocket.domain.Content;
import com.ubiquity.sprocket.domain.UserEngagement;
import com.ubiquity.sprocket.repository.hbase.ContentPK;

public class ContentRepositoryTest {

	private ContentRepository contentRepository;
	private Content activityContent, placeContent, videoContent;

	private Logger log = LoggerFactory.getLogger(getClass());

	@BeforeClass
	public static void setUpServices() throws ConfigurationException {
		Configuration config = new PropertiesConfiguration("test.properties");
		HBaseTableConnectionFactory.initialize(config);
	}

	@Before
	public void setUp() {
		User owner = TestUserFactory.createUserProxy();

		contentRepository = new ContentRepositoryHBaseImpl();

		// activity
		Activity activity = TestActivityFactory.createActivityWithMininumRequirements(owner, ExternalNetwork.Facebook);
		activityContent = new Content.Builder()
		.activity(activity)
		.contentId(new ContentPK(activity.getExternalNetwork(), activity.getExternalIdentifier()))
		.build();

		// video
		VideoContent video = TestVideoContentFactory.createVideoContentWithMininumRequiredFields(owner, ExternalNetwork.Facebook);
		videoContent = new Content.Builder()
		.videoContent(video)
		.contentId(new ContentPK(video.getExternalNetwork(), video.getVideo().getItemKey()))
		.build();

		// place
		Place business = TestPlaceFactory.createLosAngelesAndNeighborhoodsAndBusiness()
				.getChildren().iterator().next().getChildren().iterator().next();
		// set the place id because we identify engaged records by it
		business.setPlaceId(Math.abs(new java.util.Random().nextLong()));
		placeContent = new Content.Builder()
		.place(business)
		.contentId(new ContentPK(business.getExternalNetwork(), business.getExternalIdentifier()))
		.build();

		contentRepository.create(activityContent);
		contentRepository.create(videoContent);
		contentRepository.create(placeContent);

	}

	@Test
	public void testUpdateContentEngagement() {
		UserEngagement engagement = new UserEngagement.Builder()
		.userId(Math.abs(new java.util.Random().nextLong()))
		.timestamp(System.currentTimeMillis())
		.contentId(activityContent.getContentId())
		.build();
		contentRepository.addUserEngagement(engagement);

		// now get the count
		Long count = contentRepository.getEngagementCount(activityContent.getContentId());
		Assert.assertEquals(1l, count.longValue());

		// two more times for the same user
		contentRepository.addUserEngagement(engagement);
		contentRepository.addUserEngagement(engagement);
		count = contentRepository.getEngagementCount(activityContent.getContentId());
		Assert.assertEquals(3l, count.longValue());

		// add from another user
		engagement = new UserEngagement.Builder()
		.userId(Math.abs(new java.util.Random().nextLong()))
		.timestamp(System.currentTimeMillis())
		.contentId(activityContent.getContentId())
		.build();
		contentRepository.addUserEngagement(engagement);

		count = contentRepository.getEngagementCount(activityContent.getContentId());
		Assert.assertEquals(4l, count.longValue());

		// add engagement for a group
		String groupMembership = UUID.randomUUID().toString();
		engagement = new UserEngagement.Builder()
		.userId(Math.abs(new java.util.Random().nextLong()))
		.timestamp(System.currentTimeMillis())
		.contentId(activityContent.getContentId())
		.groupMembership(groupMembership)
		.build();
		contentRepository.addUserEngagement(engagement);

		// count for the group
		count = contentRepository.getEngagementCount(activityContent.getContentId(), groupMembership);
		Assert.assertEquals(1l, count.longValue());



		// now create 20 activity content items
		Content[] contentArray = new Content[20];
		for(int i = 0; i < 20; i++) {
			Content content = createTestActivityContent();
			contentRepository.create(content);
			contentArray[i] = content;
			log.info("created {}", content.getContentId());
		}

		String groupA = UUID.randomUUID().toString();
		UserEngagement groupAengagement = new UserEngagement.Builder()
		.userId(Math.abs(new java.util.Random().nextLong()))
		.timestamp(System.currentTimeMillis())
		.contentId(contentArray[3].getContentId())
		.groupMembership(groupA)
		.build();
		contentRepository.addUserEngagement(groupAengagement);

		groupAengagement = new UserEngagement.Builder()
		.userId(Math.abs(new java.util.Random().nextLong()))
		.timestamp(System.currentTimeMillis())
		.contentId(contentArray[4].getContentId())
		.groupMembership(groupA)
		.build();
		contentRepository.addUserEngagement(groupAengagement);


		// have group A tap the first 10 once, and then the last 3 5 times each
		List<Content> mostEngaged = contentRepository.findMostEngagedByGroup(groupA, 5);
		Assert.assertEquals(2, mostEngaged.size());

		for(int i = 0;  i < 3; i++) {
			// add 3 more to the same content and ensure that it is the first in the order
			groupAengagement = new UserEngagement.Builder()
			.userId(Math.abs(new java.util.Random().nextLong()))
			.timestamp(System.currentTimeMillis())
			.contentId(contentArray[4].getContentId())
			.groupMembership(groupA)
			.build();
			contentRepository.addUserEngagement(groupAengagement);
		}
		
		mostEngaged = contentRepository.findMostEngagedByGroup(groupA, 5);
		Assert.assertEquals(2, mostEngaged.size());
		
		// assert correct order
		Content theMostest = mostEngaged.get(0);
		Assert.assertEquals(theMostest.getContentId().toString(), contentArray[4].getContentId().toString());


	}


	@Test
	public void testCreateContent() {
		
		
		// read back in to validate the write completed
		Content persisted = contentRepository.read(activityContent.getContentId());
		Assert.assertNotNull(persisted.getActivity());
		Assert.assertNotNull(persisted.getActivity().getTitle());
		Assert.assertNotNull(persisted.getActivity().getBody());
		Assert.assertNotNull(persisted.getActivity().getExternalIdentifier());
		Assert.assertNotNull(persisted.getActivity().getExternalNetwork());
		Assert.assertNotNull(persisted.getActivity().getOwner());

		persisted = contentRepository.read(videoContent.getContentId());
		Assert.assertNotNull(persisted.getVideoContent());
		Assert.assertNotNull(persisted.getVideoContent().getTitle());
		Assert.assertNotNull(persisted.getVideoContent().getDescription());
		Assert.assertNotNull(persisted.getVideoContent().getVideo().getItemKey());
		Assert.assertNotNull(persisted.getVideoContent().getExternalNetwork());
		Assert.assertNotNull(persisted.getVideoContent().getOwner());

		persisted = contentRepository.read(placeContent.getContentId());
		Assert.assertNotNull(persisted.getPlace());
		Assert.assertNotNull(persisted.getPlace().getName());
		Assert.assertNotNull(persisted.getPlace().getDescription());
		Assert.assertNotNull(persisted.getPlace().getExternalIdentifier());
		Assert.assertNotNull(persisted.getPlace().getExternalNetwork());

		

	}

	private Content createTestActivityContent() {
		// activity
		Activity activity = TestActivityFactory.createActivityWithMininumRequirements(null, ExternalNetwork.Facebook);
		Content content = new Content.Builder()
		.activity(activity)
		.contentId(new ContentPK(activity.getExternalNetwork(), activity.getExternalIdentifier()))
		.build();

		return content;

	}
}
