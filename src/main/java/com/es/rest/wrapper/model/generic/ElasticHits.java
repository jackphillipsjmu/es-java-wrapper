package com.es.rest.wrapper.model.generic;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * Class represents the "hits" secton of a Query response from Elasticsearch. 
 * Typical JSON formatting of response consist of the following:
 * 
 * <pre>
 *  {
 *   	"hits": [
 *   		{@link ElasticHit}
 *   	],
 *   	"total": <i>{@link Long}</i>,,
 *   	"max_score": <i>{@link Double}</i>,
 *  }
 * </pre>
 * 
 * @author Jack Phillips
 * @param <T> generic type
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElasticHits<T> {
	@JsonProperty("hits")
	private List<ElasticHit<T>> elasticHits;
	
	@JsonProperty("total")
	private Long total;
	
	@JsonProperty("max_score")
	private Double maxScore;

	public List<ElasticHit<T>> getElasticHits() {
		return elasticHits;
	}

	public void setElasticHits(List<ElasticHit<T>> elasticHits) {
		this.elasticHits = elasticHits;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Double getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(Double maxScore) {
		this.maxScore = maxScore;
	}
}
