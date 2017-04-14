package com.es.rest.wrapper.model.generic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * Class representing a "hit" in Elasticsearch, JSON representation:
 * 
 * <pre>
 *  {
 *  	"_index": {@link String},
 *       "_type": {@link String},
 *       "_id": {@link String},
 *       "_score": {@link Double},
 *       "_source": {{@link T}}
 * }
 * </pre>
 * 
 * @author Jack Phillips
 * @param <T> generic type
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElasticHit<T> {
	@JsonProperty(value = "_index")
    private String index;
 
    @JsonProperty(value = "_type")
    private String type;
 
    @JsonProperty(value = "_id")
    private String id;
 
    @JsonProperty(value = "_score")
    private Double score;
    
    @JsonProperty(value = "_source")
    private T source;

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

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public T getSource() {
		return source;
	}

	public void setSource(T source) {
		this.source = source;
	}
}
