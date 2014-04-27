package com.ubiquity.social.api.contact;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ubiquity.media.domain.Image;
import com.ubiquity.social.domain.SocialProviderType;

public class YahooContact {
	@Expose
	private Long contactId;

	@SerializedName("id")
	private String socialContactId;
	
	@SerializedName("displayName")
	private String displayName;
	
	//@SerializedName("first_name")
	private String firstName;
    
	//@SerializedName("last_name")
	private String lastName;
	
	//@SerializedName("username")
	private String userName ;

	@SerializedName("image")
	private Image image ;
    
	@SerializedName("url")
	private String profileUrl;

	
	private Long ownerId;

	private SocialProviderType socialProviderType;
    
	private String socialProviderIdentifier;
	
    public YahooContact() {
		// TODO Auto-generated constructor stub
    	this.socialProviderType = SocialProviderType.Yahoo;
    	this.socialProviderIdentifier = SocialProviderType.Yahoo.name();
    }
    
    
	public Long getContactId() {
		return contactId;
	}

    
	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}

	public String getDisplayName() {
		return displayName;
	}

	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	
	public String getFirstName() {
		return firstName;
	}

	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	
	public String getLastName() {
		return lastName;
	}

	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	
	public String getUserName() {
		return userName;
	}

	
	public void setUserName(String userName) {
		this.userName = userName;
	}

	
	public Image getImage() {
		return image;
	}

	
	public void setImage(Image image) {
		this.image = image;
	}

	
	public String getProfileUrl() {
		return profileUrl;
	}

	
	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}

	
	public Long getOwnerId() {
		return ownerId;
	}

	
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	
	public SocialProviderType getSocialProviderType() {
		return socialProviderType;
	}

	
	public void setSocialProviderType(SocialProviderType socialProviderType) {
		this.socialProviderType = socialProviderType;
	}

	
	public String getSocialProviderIdentifier() {
		return socialProviderIdentifier;
	}

	
	public void setSocialProviderIdentifier(String socialProviderIdentifier) {
		this.socialProviderIdentifier = socialProviderIdentifier;
	}
}
