package com.es.rest.wrapper.model.generic;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeleteResponse<T> {
	@JsonProperty(value = "_index")
	private String index;

	@JsonProperty(value = "_type")
	private String type;

	@JsonProperty(value = "_id")
	private String id;

	@JsonProperty(value = "_version")
	private long version;

	@JsonProperty(value = "result")
	private String result;

	@JsonProperty(value = "_shards")
	private ElasticShards shards;

	@JsonProperty(value = "found")
	private Boolean found;

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public ElasticShards getShards() {
		return shards;
	}

	public void setShards(ElasticShards shards) {
		this.shards = shards;
	}

	public Boolean getFound() {
		return found;
	}

	public void setFound(Boolean found) {
		this.found = found;
	}
}
