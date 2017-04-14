package com.es.rest.wrapper.enumeration;

/**
 * Enum constants to provide typical REST operations performed in Elasticsearch 
 * 
 * @author Jack Phillips
 */
public enum UrlComponentEnum {
	CANCEL("_cancel"),
	CONFLICTS("conflicts="),
	COUNT("_count"),
	DELETE_BY_QUERY("_delete_by_query"),
	FILTER_PATH("filter_path="),
	FWD_SLASH("/"),
	PRETTY("pretty="),
	Q("q="),
	QUERY("query"),
	REFRESH("refresh"),
	RETHROTTLE("_rethrottle"),
	ROUTING("routing="),
	RQST_PER_SEC("requests_per_second="),
	SCROLL_SIZE("scroll_size="),
	SEARCH("_search"),
	SIZE("size="),
	SLICES("slices="),
	SOURCE("_source"),
	SRC_EXCLUDE("_source_exclude="),
	SRC_INCLUDE("_source_include=");
	
	private String value;
	
	UrlComponentEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
