package com.ubiquity.sprocket.api.dto.model;


public class DocumentDto {

	private String dataType;
	private Integer rank;
	private Object data;

	public String getDataType() {
		return dataType;
	}

	public Integer getRank() {
		return rank;
	}

	public Object getData() {
		return data;
	}

	public static class Builder {
		private String dataType;
		private Integer rank;
		private Object data;

		public Builder dataType(String dataType) {
			this.dataType = dataType;
			return this;
		}

		public Builder rank(Integer rank) {
			this.rank = rank;
			return this;
		}

		public Builder data(Object data) {
			this.data = data;
			return this;
		}

		public DocumentDto build() {
			return new DocumentDto(this);
		}
	}

	private DocumentDto(Builder builder) {
		this.dataType = builder.dataType;
		this.rank = builder.rank;
		this.data = builder.data;
	}
}