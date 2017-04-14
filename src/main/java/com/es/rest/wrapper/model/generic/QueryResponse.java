package com.es.rest.wrapper.model.generic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * Class represents a Query response from Elasticsearch.Typical JSON formatting of response consist
 * of the following:
 * 
 * <pre>
 * {
 *     "took": <i>{@link Long}</i>,
 *     "timed_out": <i>{@link Boolean}></i>,
 *     "_shards": <i>{@link ElasticShards}}</i>
 *     "hits": <i>{@link ElasticHits}</i>
 * }
 * </pre>
 * 
 * @author Jack Phillips
 * @param <T> the type of the value being queried 
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class QueryResponse<T> {
	@JsonProperty(value = "took")
	private Long took;
	
	@JsonProperty(value = "timed_out")
    private Boolean timedOut;

    @JsonProperty(value = "_shards")
    private ElasticShards shards;
    
    @JsonProperty(value = "hits")
    private ElasticHits<T> hits;

    /** Getters and Setters **/
    public ElasticHits<T> getHits() {
        return hits;
    }

    public void setHits(ElasticHits<T> hits) {
        this.hits = hits;
    }

    public Long getTook() {
        return took;
    }

    public void setTook(Long took) {
        this.took = took;
    }

    public ElasticShards getShards() {
        return shards;
    }

    public void setShards(ElasticShards shards) {
        this.shards = shards;
    }

    public Boolean getTimedOut() {
        return timedOut;
    }

    public void setTimedOut(Boolean timedOut) {
        this.timedOut = timedOut;
    }
}
