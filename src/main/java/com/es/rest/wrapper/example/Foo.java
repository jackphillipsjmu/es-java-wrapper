package com.es.rest.wrapper.example;

import com.es.rest.wrapper.annotation.ElasticDocument;

/**
 * Example class to represent Elasticsearch document
 *  
 * @author Jack Phillips
 */
@ElasticDocument(indexName = "foo", type = "bar")
public class Foo {
	
	private String barVal;
	
	public Foo(){}

	public Foo(String barVal) {
		this.barVal = barVal;
	}
	
	public String getBarVal() {
		return barVal;
	}

	public void setBarVal(String barVal) {
		this.barVal = barVal;
	}
	
}
