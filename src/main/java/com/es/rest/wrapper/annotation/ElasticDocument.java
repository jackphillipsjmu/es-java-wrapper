package com.es.rest.wrapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for Elasticsearch documents
 * 
 * @author Jack Phillips
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface ElasticDocument {
	/**
	 * Elasticsearch index name
	 * @return String indexName
	 */
	String indexName();
	
	/**
	 * Elasticsearch type name
	 * @return String type
	 */
	String type() default "";
}
