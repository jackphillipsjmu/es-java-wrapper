package com.es.rest.wrapper.model.generic;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CountResponse {
	@JsonProperty(value = "_shards")
	private ElasticShards shards;

	private long count;

	public ElasticShards getShards() {
		return shards;
	}

	public void setShards(ElasticShards shards) {
		this.shards = shards;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}
}
