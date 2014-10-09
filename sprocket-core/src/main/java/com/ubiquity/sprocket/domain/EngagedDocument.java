package com.ubiquity.sprocket.domain;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.ubiquity.identity.domain.User;
import com.ubiquity.integration.domain.Activity;
import com.ubiquity.integration.domain.Message;
import com.ubiquity.integration.domain.VideoContent;

@Entity
@DiscriminatorValue("document")
public class EngagedDocument extends EngagedItem {
	
	@Column(name = "search_term")
	private String searchTerm;
	
	@Column(name = "document_data_type")
	private String documentDataType;
	
	@ManyToOne
	@JoinColumn(name = "activity_id")
	private Activity activity;
	
	@ManyToOne
	@JoinColumn(name = "video_content_id")	
	private VideoContent videoContent;
	
	@ManyToOne
	@JoinColumn(name = "message_id")	
	private Message message;

	
	public EngagedDocument(User user, String searchTerm,
			Activity activity) {
		super(user);
		this.searchTerm = searchTerm;
		this.documentDataType = activity.getClass().getSimpleName();
		this.activity = activity;
	}

	public EngagedDocument(User user, String searchTerm,
			VideoContent videoContent) {
		super(user);
		this.searchTerm = searchTerm;
		this.documentDataType = videoContent.getClass().getSimpleName();
		this.videoContent = videoContent;
	}

	/***
	 * Required by JPA
	 */
	protected EngagedDocument() {}
	
	public EngagedDocument(User user, String searchTerm,
			Message message) {
		super(user);
		this.searchTerm = searchTerm;
		this.documentDataType = message.getClass().getSimpleName();
		this.message = message;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public String getDocumentDataType() {
		return documentDataType;
	}

	public Activity getActivity() {
		return activity;
	}

	public VideoContent getVideoContent() {
		return videoContent;
	}

	public Message getMessage() {
		return message;
	}
	
	

}
