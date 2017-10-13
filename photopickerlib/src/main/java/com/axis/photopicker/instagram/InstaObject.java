package com.axis.photopicker.instagram;

import java.util.List;

public class InstaObject {
	private List<Data> data;
	private Meta meta;
	private Pagination pagination;

	public List<Data> getData() {
		return this.data;
	}

	public void setData(List<Data> data) {
		this.data = data;
	}

	public Meta getMeta() {
		return this.meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	public Pagination getPagination() {
		return this.pagination;
	}

	public void setPagination(Pagination pagination) {
		this.pagination = pagination;
	}

	class Pagination {

		private String next_url;
		private String next_max_id;

		public String getNext_url() {
			return next_url;
		}

		public void setNext_url(String next_url) {
			this.next_url = next_url;
		}

		public String getNext_max_id() {
			return next_max_id;
		}

		public void setNext_max_id(String next_max_id) {
			this.next_max_id = next_max_id;
		}
	}

	class Meta {
		private int code;

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}
	}

	class Data {
		private Images images;

		public Images getImages() {
			return images;
		}

		public void setImages(Images images) {
			this.images = images;
		}

		class Images {

			private Resolution low_resolution;
			private Resolution thumbnail;
			private Resolution standard_resolution;

			public Resolution getLow_resolution() {
				return low_resolution;
			}

			public void setLow_resolution(Resolution low_resolution) {
				this.low_resolution = low_resolution;
			}

			public Resolution getThumbnail() {
				return thumbnail;
			}

			public void setThumbnail(Resolution thumbnail) {
				this.thumbnail = thumbnail;
			}

			public Resolution getStandard_resolution() {
				return standard_resolution;
			}

			public void setStandard_resolution(Resolution standard_resolution) {
				this.standard_resolution = standard_resolution;
			}

			class Resolution {
				
				private String url;

				public String getUrl() {
					return url;
				}

				public void setUrl(String url) {
					this.url = url;
				}
				
			}
		}
	}
}
