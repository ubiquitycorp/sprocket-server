package com.ubiquity.sprocket.api.dto.model;

import java.util.LinkedList;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.ubiquity.sprocket.api.validation.EngagementValidation;

/***
 * Dto representing an activity (i.e. a post, news feed item)
 * 
 * @author chris
 * 
 */
public class ActivityDto {

	private Long activityId;

	/***
	 * Constraints applied when this DTO is used to carry an input payload;
	 * these 3 properties are needed to determine if a record has already been
	 * persisted
	 */
	@NotNull(groups = { EngagementValidation.class })
	private Integer externalNetworkId;

	@NotNull(groups = { EngagementValidation.class })
	private String type;

	@NotNull(groups = { EngagementValidation.class })
	private String externalIdentifier;

	private String title;
	private String body;
	private Long date;
	
	private RatingDto rating;
	
	private List<CommentDto> comments = new LinkedList<CommentDto>();
	
	private List<InterestDto> interests = new LinkedList<InterestDto>();

	@NotNull(groups = { EngagementValidation.class })
	private ContactDto postedBy;

	private ImageDto photo;
	private VideoDto video;

	private String link;

	private String category;

	private Long ownerId;

	public Long getActivityId() {
		return activityId;
	}

	public Integer getExternalNetworkId() {
		return externalNetworkId;
	}

	public String getLink() {
		return link;
	}

	public ImageDto getPhoto() {
		return photo;
	}

	public VideoDto getVideo() {
		return video;
	}

	public String getType() {
		return type;
	}

	public String getTitle() {
		return title;
	}

	public String getBody() {
		return body;
	}

	public Long getDate() {
		return date;
	}

	public ContactDto getPostedBy() {
		return postedBy;
	}

	public Integer externalNetworkId() {
		return externalNetworkId;
	}

	public String getExternalIdentifier() {
		return externalIdentifier;
	}

	public String getCategory() {
		return category;
	}

	public Long getOwnerId() {
		return ownerId;
	}
	
	public RatingDto getRating() {
		return rating;
	}

	public List<CommentDto> getComments() {
		return comments;
	}

	public List<InterestDto> getInterests() {
		return interests;
	}


	public static class Builder {
		private String title;
		private String body;
		private Long date;
		private ContactDto postedBy;
		private ImageDto photo;
		private VideoDto video;
		private Integer externalNetworkId;
		private String type;
		private String link;
		private String externalIdentifier;
		private String category;
		private Long ownerId;
		private RatingDto rating;
		private List<CommentDto> comments = new LinkedList<CommentDto>();
		private List<InterestDto> interests = new LinkedList<InterestDto>();

		public Builder title(String title) {
			this.title = title;
			return this;
		}

		public Builder body(String body) {
			this.body = body;
			return this;
		}

		public Builder date(Long date) {
			this.date = date;
			return this;
		}

		public Builder postedBy(ContactDto postedBy) {
			this.postedBy = postedBy;
			return this;
		}

		public Builder photo(ImageDto photo) {
			this.photo = photo;
			return this;
		}

		public Builder video(VideoDto video) {
			this.video = video;
			return this;
		}

		public Builder externalNetworkId(Integer externalNetworkId) {
			this.externalNetworkId = externalNetworkId;
			return this;
		}

		public Builder ownerId(Long ownerId) {
			this.ownerId = ownerId;
			return this;
		}

		public Builder type(String type) {
			this.type = type;
			return this;
		}

		public Builder link(String link) {
			this.link = link;
			return this;
		}

		public Builder externalIdentifier(String externalIdentifier) {
			this.externalIdentifier = externalIdentifier;
			return this;
		}

		public Builder category(String category) {
			this.category = category;
			return this;
		}

		public Builder addComment(CommentDto commentDto) {
			this.comments.add(commentDto);
			return this;
		}
		
		public Builder addInterest(InterestDto interestDto) {
			this.interests.add(interestDto);
			return this;
		}
		
		public Builder rating(RatingDto rating) {
			this.rating = rating;
			return this;
		}
		public ActivityDto build() {
			return new ActivityDto(this);
		}
	}

	private ActivityDto(Builder builder) {
		this.title = builder.title;
		this.body = builder.body;
		this.date = builder.date;
		this.postedBy = builder.postedBy;
		this.photo = builder.photo;
		this.video = builder.video;
		this.externalNetworkId = builder.externalNetworkId;
		this.type = builder.type;
		this.link = builder.link;
		this.externalIdentifier = builder.externalIdentifier;
		this.category = builder.category;
		this.ownerId = builder.ownerId;
		this.rating = builder.rating;
		this.comments = builder.comments;
		this.interests = builder.interests;
	}

	@Override
	public String toString() {
		return "ActivityDto [activityId=" + activityId + ", title=" + title
				+ ", body=" + body + ", date=" + date + ", postedBy="
				+ postedBy + ", photo=" + photo + ", video=" + video
				+ ", externalNetworkId=" + externalNetworkId + ", type=" + type
				+ ", link=" + link + ", externalIdentifier="
				+ externalIdentifier + "]";
	}

}
