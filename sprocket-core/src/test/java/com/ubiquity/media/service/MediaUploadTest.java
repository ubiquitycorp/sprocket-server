package com.ubiquity.media.service;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.redis.JedisConnectionFactory;
import com.ubiquity.integration.api.SocialAPIFactory;
import com.ubiquity.media.domain.AudioTrack;
import com.ubiquity.media.domain.Image;

public class MediaUploadTest {
	
private static Logger log = LoggerFactory.getLogger(MediaUploadTest.class);
	
	private static MediaService mediaService;
	
	@BeforeClass
	public static void setUp() throws Exception {
		
		Configuration configuration = new PropertiesConfiguration("test.properties");
		
		mediaService = new MediaService(configuration);
		
		JedisConnectionFactory.initialize(configuration);
		
		SocialAPIFactory.initialize(configuration);
	}
	
	@Test
	public void testUploadImage() {
		Image image = new Image.Builder().itemKey("welcome.jpg").build();
		InputStream inputStream;
		inputStream = this.getClass().getClassLoader().getResourceAsStream("welcome.jpg");
		image.setInputStream(inputStream);
		mediaService.create(image);
		log.debug("image upload url is " + image.getUrl());
	}
	
	//@Test
	public void testUploadAudio() throws UnsupportedAudioFileException, IOException {
		AudioTrack audio = new AudioTrack.Builder().itemKey("Black_and_Blue.mp3").build();
		InputStream inputStream;
		inputStream = this.getClass().getClassLoader().getResourceAsStream("Black_and_Blue.mp3");
		audio.setInputStream(inputStream);
		mediaService.create(audio);
		log.debug("image upload url is " + audio.getUrl());
	}

}
