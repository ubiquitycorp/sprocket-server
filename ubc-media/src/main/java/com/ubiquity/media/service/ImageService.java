package com.ubiquity.media.service;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.cloud.APICredentials;
import com.niobium.repository.cloud.RemoteAssetRepository;
import com.niobium.repository.cloud.RemoteAssetRepositoryS3Impl;
import com.ubiquity.media.domain.Image;

/***
 * 
 * Image service providing image uploading funcitonality
 * 
 * @author chris
 *
 */
public class ImageService {

	public static final String SERVICE_KEY = ImageService.class.getName();

	private Logger log = LoggerFactory.getLogger(getClass());

	private RemoteAssetRepository repository;
	private String bucket;

	/***
	 * Initializes an image service with a configuration.
	 * 
	 * @param configuration
	 */
	public ImageService(Configuration configuration) {

		bucket = configuration.getString("s3.bucket");

		String accessKey = configuration.getString("s3.api.access");
		String secretKey = configuration.getString("s3.api.secret");
		repository = new RemoteAssetRepositoryS3Impl(bucket, 
				new APICredentials.Builder().accessKey(accessKey).secretKey(secretKey).build());

		log.info("Authenticated to S3 Repository bucket {} with access key {}", bucket, accessKey);
	}

	/***
	 * Uploads an image to a CDN and sets the URL property with the new resource location
	 * @param image
	 */
	public void create(Image image) {
		repository.create(image);
		// now set the url
		image.setUrl("http://"+bucket+".s3.amazonaws.com/"+image.getItemKey());
		
	}


}
