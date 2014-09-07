package com.ubiquity.sprocket.domain;

import java.util.Locale;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Index;
/***
 * Entity holding a place. A place can be a city, state, or anything that has a name and a computed lat/lon bounding box
 * 
 * @author chris
 *
 */
@Entity
@AttributeOverrides({
	@AttributeOverride(name = "boundingBox.center.latitude", column = @Column(name = "box_center_latitude")),
	@AttributeOverride(name = "boundingBox.lowerLeft.latitude", column = @Column(name = "box_lower_left_latitude")),
	@AttributeOverride(name = "boundingBox.lowerRight.latitude", column = @Column(name = "box_lower_right_latitude")),
	@AttributeOverride(name = "boundingBox.upperLeft.latitude", column = @Column(name = "box_upper_left_latitude")),
	@AttributeOverride(name = "boundingBox.upperRight.latitude", column = @Column(name = "box_upper_right_latitude")),
	@AttributeOverride(name = "boundingBox.center.longitude", column = @Column(name = "box_center_longitude")),
	@AttributeOverride(name = "boundingBox.lowerLeft.longitude", column = @Column(name = "box_lower_left_longitude")),
	@AttributeOverride(name = "boundingBox.lowerRight.longitude", column = @Column(name = "box_lower_right_longitude")),
	@AttributeOverride(name = "boundingBox.upperLeft.longitude", column = @Column(name = "box_upper_left_longitude")),
	@AttributeOverride(name = "boundingBox.upperRight.longitude", column = @Column(name = "box_upper_right_longitude")),
	@AttributeOverride(name = "boundingBox.center.altitude", column = @Column(name = "box_center_altitude")),
	@AttributeOverride(name = "boundingBox.lowerLeft.altitude", column = @Column(name = "box_lower_left_altitude")),
	@AttributeOverride(name = "boundingBox.lowerRight.altitude", column = @Column(name = "box_lower_right_altitude")),
	@AttributeOverride(name = "boundingBox.upperLeft.altitude", column = @Column(name = "box_upper_left_altitude")),
	@AttributeOverride(name = "boundingBox.upperRight.altitude", column = @Column(name = "box_upper_right_altitude")),
})
@Table(name = "place", indexes = {
		@Index(name="idx_place_name_locale", columnList = "name, locale", unique = true)
		})
public class Place {

	@Id
	@GeneratedValue
	@Column(name = "place_id")
	private Long placeId;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "locale", nullable = false)
	private Locale locale;

	@Embedded
	private Geobox boundingBox;

	
	public Geobox getBoundingBox() {
		return boundingBox;
	}

	public Long getPlaceId() {
		return placeId;
	}

	public String getName() {
		return name;
	}

	public Locale getLocale() {
		return locale;
	}

	public static class Builder {
		private String name;
		private Locale locale;
		private Geobox boundingBox;

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder locale(Locale locale) {
			this.locale = locale;
			return this;
		}

		public Builder boundingBox(Geobox boundingBox) {
			this.boundingBox = boundingBox;
			return this;
		}

		public Place build() {
			return new Place(this);
		}
	}

	private Place(Builder builder) {
		this.name = builder.name;
		this.locale = builder.locale;
		this.boundingBox = builder.boundingBox;
	}
}
