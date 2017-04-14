package com.es.rest.wrapper.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.es.rest.wrapper.annotation.ElasticDocument;
import com.es.rest.wrapper.enumeration.UrlComponentEnum;
import com.es.rest.wrapper.model.generic.CountResponse;
import com.es.rest.wrapper.model.generic.DeleteResponse;
import com.es.rest.wrapper.model.generic.IndexResponse;
import com.es.rest.wrapper.model.generic.QueryResponse;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Abstract class that includes typical CRUD operations with Elasticsearch
 *  
 * @author Jack Phillips
 */
@Service
public abstract class ElasticsearchService {
	protected final Log logger = LogFactory.getLog(getClass());

	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String DELETE = "DELETE";

	@Autowired
	private RestClient restClient;
	private ObjectMapper mapper;
	private Response response;
	private QueryResponse<?> asyncQueryResponse;
	
	/**
	 * Returns {@link QueryResponse} object retrieved from the specified query.
	 * Uses synchronous communication.
	 * 
	 * @param clazz - Java class that is annotated with {@link ElasticDocument}
	 * @param query - String that contains the query to send to Elasticsearch
	 * @return QueryResponse 
	 * @throws IOException
	 */
	public QueryResponse<?> getSync(Class<?> clazz, String query) throws IOException {
		// Cannot create GET if no connection to Elasticsearch
		if (!restClientExists()) 
			return null;

		mapper = new ObjectMapper();
		response = null;
		QueryResponse<?> queryResponse = null;

		query = buildQueryHelper(clazz, query);
		response = restClient.performRequest(GET, query);
		queryResponse = mapper.readValue(EntityUtils.toString(response.getEntity()), QueryResponse.class);

		return queryResponse;
	}

	/**
	 * Returns {@link QueryResponse} object retrieved from the specified query.
	 * Uses asynchronous communication.
	 * 
	 * @param clazz - Java class that is annotated with {@link ElasticDocument}
	 * @param query - String that contains the query to send to Elasticsearch
	 * @return QueryResponse 
	 */
	public QueryResponse<?> performAsyncRequest(Class<?> clazz, String query) {
		if (!restClientExists()) 
			return null;

		mapper = new ObjectMapper();
		response = null;
		asyncQueryResponse = null;

		query = buildQueryHelper(clazz, query);

		restClient.performRequestAsync(
				GET,
				query,
				new ResponseListener() {

					@Override
					public void onSuccess(Response response) {
						try {
							asyncQueryResponse = mapper.readValue(EntityUtils.toString(response.getEntity()), QueryResponse.class);
						} catch (IOException e) {
							logger.error("ERROR: Could not execute Asynchronous GET request: " + e.getMessage());
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(Exception exception) {
						logger.error("FAILURE: Could not execute Asynchronous GET request: " + exception.getMessage());
						exception.printStackTrace();
					}
				});

		return asyncQueryResponse;
	}

	/**
	 * Returns an instance of the passed in Class if there is a Elasticsearch document with the 
	 * specified ID.
	 * 
	 * @param clazz - Java class that is annotated with {@link ElasticDocument}
	 * @param id - Document ID
	 * @return Object - new instance of Class parameter
	 * @throws IOException
	 */
	public Object findOne(Class<?> clazz, String id) throws IOException {
		// Cannot create GET if no connection to Elasticsearch
		if (!restClientExists()) 
			return null;
		
		mapper = new ObjectMapper();
		response = null;

		id += "/" + UrlComponentEnum.SOURCE.getValue();
		String query = buildQueryHelper(clazz, id);
		response = restClient.performRequest(GET, query);

		return mapper.readValue(EntityUtils.toString(response.getEntity()), clazz);
	}

	/**
	 * Returns a list of objects retrieved from Elasticsearch associated 
	 * with the corresponding Class<?> clazz.
	 * 
	 * @param clazz - Java class that is annotated with {@link ElasticDocument}
	 * @return Potentially empty List of Objects retrieved from Elasticsearch 
	 * @throws IOException
	 */
	public List<Object> findAll(Class<?> clazz) throws IOException {
		List<Object> objList = new ArrayList<Object>();

		// Cannot create GET if no connection to Elasticsearch
		if (!restClientExists()) 
			return objList;

		mapper = new ObjectMapper();
		response = null;

		// Get all values associated with object
		String query = buildQueryHelper(clazz, UrlComponentEnum.SEARCH.getValue());
		response = restClient.performRequest(GET, query);
		
		// Retrieve JSON Node from the response
		JsonNode treeNode = mapper.readTree(EntityUtils.toString(response.getEntity()));

		// If the Json Node is not null try to find the _source values and add to map
		if (!Objects.isNull(treeNode)) {
			List<JsonNode> sourceNodes = treeNode.findValues(UrlComponentEnum.SOURCE.getValue());
			for (JsonNode curNode : sourceNodes) {
				objList.add(mapper.treeToValue(curNode, clazz));
			}
		}		
		return objList;
	}
	
	/**
	 * Returns all Documents in {@link QueryResponse} object thats associated with clazz parameter.
	 * Uses synchronous communication.
	 * 
	 * @param clazz - Java class that is annotated with {@link ElasticDocument}
	 * @return QueryResponse 
	 * @throws IOException
	 * @throws ParseException
	 */
	public QueryResponse<?> getAllSync(Class<?> clazz) throws IOException, ParseException {
		return getSync(clazz, UrlComponentEnum.SEARCH.getValue()); 
	}

	/**
	 * Returns all Documents in {@link QueryResponse} object thats associated with clazz parameter.
	 * Uses Asynchronous communication.
	 * 
	 * @param clazz - Java class that is annotated with {@link ElasticDocument}
	 * @return QueryResponse 
	 * @throws IOException
	 * @throws ParseException
	 */
	public QueryResponse<?> getAllAsync(Class<?> clazz) throws IOException, ParseException {
		return performAsyncRequest(clazz, UrlComponentEnum.SEARCH.getValue());
	}

	/**
	 * Returns {@link IndexResponse} for POST requests without a specified ID.
	 * 
	 * @param obj - Java object that is annotated with {@link ElasticDocument}
	 * @return IndexResponse<?> 
	 * @throws IOException
	 */
	public IndexResponse<?> postSync(Object obj) throws IOException {
		return postSync(obj, null);
	}

	/**
	 * Returns {@link IndexResponse} for POST requests.
	 * 
	 * @param obj - Java object that is annotated with {@link ElasticDocument}
	 * @param id - Elasticsearch document ID, can be NULL if no ID is specified
	 * @return IndexResponse<?> 
	 * @throws IOException
	 */
	public IndexResponse<?> postSync(Object obj, String id) throws IOException {
		// Cannot create POST if no connection to Elasticsearch
		if (!restClientExists()) 
			return null;

		mapper = new ObjectMapper();
		response = null;
		IndexResponse<?> indexResponse = null;

		ElasticDocument elasticDocument = AnnotationUtils.findAnnotation(obj.getClass(), ElasticDocument.class);
		String indexAndTypeStr = StringUtils.isEmpty(id) ? getIndexAndTypeString(elasticDocument) : getIndexAndTypeString(elasticDocument) + id;

		// Convert object to JSON String and create HTTPEntity from it
		String jsonString = mapper.writeValueAsString(obj);
		HttpEntity entity = new NStringEntity(jsonString, ContentType.APPLICATION_JSON);
		response = restClient.performRequest(
				POST,
				indexAndTypeStr,
				Collections.<String, String>emptyMap(),
				entity);
		indexResponse = mapper.readValue(EntityUtils.toString(response.getEntity()), IndexResponse.class);

		return indexResponse;
	}

	/**
	 * Returns {@link DeleteResponse} for DELETE requests.
	 * 
	 * @param clazz - Java class that is annotated with {@link ElasticDocument}
	 * @param id - Elasticsearch document ID
	 * @return DeleteResponse<?> 
	 * @throws IOException
	 */
	public DeleteResponse<?> deleteSyncById(String id, Class<?> clazz) throws IOException {
		// Cannot create DELETE if no connection to Elasticsearch
		if (!restClientExists()) 
			return null;

		mapper = new ObjectMapper();
		response = null;
		DeleteResponse<?> deleteResponse = null;

		// Pull annotation and generate DELETE request
		ElasticDocument elasticDocument = AnnotationUtils.findAnnotation(clazz, ElasticDocument.class);
		String indexAndTypeStr = getIndexAndTypeString(elasticDocument);
		if (!StringUtils.isEmpty(indexAndTypeStr) && !StringUtils.isEmpty(id)) {
			response = restClient.performRequest(DELETE, indexAndTypeStr + id); 
			deleteResponse = mapper.readValue(EntityUtils.toString(response.getEntity()), DeleteResponse.class);
		}
		return deleteResponse;
	}
	
	public Long getIndexCount(Class<?> clazz) throws IOException {
		ElasticDocument elasticDocument = AnnotationUtils.findAnnotation(clazz, ElasticDocument.class);
		if (!Objects.isNull(elasticDocument)) {
			return getIndexCount(elasticDocument.indexName());
		}
		return -1L;
	}
	
	public Long getIndexCount(String indexName) throws IOException {
		if (!restClientExists()) 
			return null;
		mapper = new ObjectMapper();
		response = null;
		
		response = restClient.performRequest(
				GET,
				indexName 
				+ UrlComponentEnum.FWD_SLASH.getValue() 
				+ UrlComponentEnum.COUNT.getValue());
		
		return mapper.readValue(response.getEntity().getContent(), CountResponse.class).getCount();
	}
	
	/**
	 * Returns ResponseEntity<?> object wrapped around a {@link QueryResponse}
	 * for GET requests. 
	 * 
	 * @param clazz - Java class that is annotated with {@link ElasticDocument}
	 * @param id - Elasticsearch document ID
	 * @return ResponseEntity<?> containing proper HTTP Status code and response body
	 * @throws IOException
	 */
	public ResponseEntity<?> getSyncResponseEntity(Class<?> clazz, String query) throws IOException {
		// Cannot create GET if no connection to Elasticsearch
		if (!restClientExists()) 
			return null;

		mapper = new ObjectMapper();
		response = null;
		
		query = buildQueryHelper(clazz, query);
		response = restClient.performRequest(GET, query);
		
		return getResponseEntity(response);
	}
	
	/**
	 * Returns ResponseEntity<?> object wrapped around a {@link IndexResponse}  
	 * for POST requests.
	 * 
	 * @param obj - Java object that is annotated with {@link ElasticDocument}
	 * @param id - Optional Elasticsearch document ID
	 * @return ResponseEntity<?> containing proper HTTP Status code and response body
	 * @throws IOException
	 */
	public ResponseEntity<?> postSyncResponseEntity(Object obj, String id) throws IOException {
		// Cannot create POST if no connection to Elasticsearch
		if (!restClientExists()) 
			return null;

		mapper = new ObjectMapper();
		response = null;

		ElasticDocument elasticDocument = AnnotationUtils.findAnnotation(obj.getClass(), ElasticDocument.class);
		String indexAndTypeStr = StringUtils.isEmpty(id) ? getIndexAndTypeString(elasticDocument) : getIndexAndTypeString(elasticDocument) + id;

		// Convert object to JSON String and create HTTPEntity from it
		String jsonString = mapper.writeValueAsString(obj);
		HttpEntity entity = new NStringEntity(jsonString, ContentType.APPLICATION_JSON);
		response = restClient.performRequest(
				POST,
				indexAndTypeStr,
				Collections.<String, String>emptyMap(),
				entity);
		
		return getResponseEntity(response);
	}
	
	/**
	 * Returns ResponseEntity<?> object wrapped around a {@link DeleteResponse}
	 * for DELETE requests.
	 * 
	 * @param clazz - Java class that is annotated with {@link ElasticDocument}
	 * @param id - Elasticsearch document ID
	 * @return ResponseEntity<?> containing proper HTTP Status code and response body
	 * @throws IOException
	 */
	public ResponseEntity<?> deleteSyncResponseEntity(Class<?> clazz, String id) throws IOException {
		// Cannot create DELETE if no connection to Elasticsearch
		if (!restClientExists()) 
			return null;

		mapper = new ObjectMapper();
		response = null;

		// Pull annotation and generate DELETE request
		ElasticDocument elasticDocument = AnnotationUtils.findAnnotation(clazz, ElasticDocument.class);
		String indexAndTypeStr = getIndexAndTypeString(elasticDocument);
		if (!StringUtils.isEmpty(indexAndTypeStr) && !StringUtils.isEmpty(id)) {
			response = restClient.performRequest(DELETE, indexAndTypeStr + id); 
		}
		return getResponseEntity(response);
	}

	/**
	 * Checks the Autowired RestClient is not null.
	 * 
	 * @return true if the object client is not null, false otherwise
	 */
	private boolean restClientExists() {
		return !Objects.isNull(restClient);
	}

	/**
	 * Pulls index name and type from annotated {@link ElasticDocument} classes
	 * 
	 * @param elasticDocument
	 * @return String representing index and type (ex. /foo/bar)
	 */
	private String getIndexAndTypeString(ElasticDocument elasticDocument) {
		StringBuilder stringBuilder = new StringBuilder();
		if (!Objects.isNull(elasticDocument)) {
			stringBuilder.append(
					UrlComponentEnum.FWD_SLASH.getValue() 
					+ elasticDocument.indexName());
			// Type is not required in ES, if none given do not set it
			if (StringUtils.isEmpty(elasticDocument.type())) {
				stringBuilder.append(
						UrlComponentEnum.FWD_SLASH.getValue());
			} else {
				stringBuilder.append(
						UrlComponentEnum.FWD_SLASH.getValue() 
						+ elasticDocument.type() 
						+ UrlComponentEnum.FWD_SLASH.getValue());
			}
		}
		return stringBuilder.toString();
	}

	private String buildQueryHelper(Class<?> clazz, String query) {
		// Pull in annotation from class if it exists to determine index and type
		ElasticDocument elasticDocument = AnnotationUtils.findAnnotation(clazz, ElasticDocument.class);
		String result = getIndexAndTypeString(elasticDocument);

		// No query provided, pass back index/type
		if (StringUtils.isEmpty(query)) {
			return result;
		}
		
		// If index/type are in the query string remove it
		query = StringUtils.replace(query, result, "");
		result += query;

		return result;
	}
	
	/**
	 * Helper method parse response from Elasticsearch and returns corresponding ResponseEntity.
	 * 
	 * @param elasticResponse - Response object from Elasticsearch
	 * @param shouldReturnSourceObj - boolean to determine whether to return all data 
	 * associated with the response or return the underlying _source object
	 * @return ResponseEntity<?> containing proper HTTP Status code and response body
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws ParseException
	 * @throws IOException
	 */
	private ResponseEntity<?> getResponseEntity(Response elasticResponse) throws JsonParseException, JsonMappingException, ParseException, IOException {
		ObjectMapper mapper = new ObjectMapper();

		if (Objects.isNull(elasticResponse)) {
			return new ResponseEntity<String>("Elasticsearch response is empty", HttpStatus.NOT_FOUND);
		} 
		
		int statusCode = elasticResponse.getStatusLine().getStatusCode();
		String requestMethod = elasticResponse.getRequestLine().getMethod();
		
		if (requestMethod.equalsIgnoreCase(GET)) {
			// GET Request routes to QueryResponse unless if we're returning the source obj
			QueryResponse<?> queryResponse = mapper.readValue(
					EntityUtils.toString(response.getEntity()), QueryResponse.class);
			return new ResponseEntity<QueryResponse<?>>(
					queryResponse, HttpStatus.valueOf(statusCode));
		} else if (requestMethod.equalsIgnoreCase(POST)) {
			// POST Request routes to IndexResponse
			IndexResponse<?> indexResponse = mapper.readValue(EntityUtils.toString(
					response.getEntity()), IndexResponse.class);
			return new ResponseEntity<IndexResponse<?>>(
					indexResponse, HttpStatus.valueOf(statusCode));
		} else if (requestMethod.equalsIgnoreCase(DELETE)) {
			// DELETE Request routes to DeleteResponse
			DeleteResponse<?> deleteResponse = mapper.readValue(EntityUtils.toString(
					response.getEntity()), DeleteResponse.class);
			return new ResponseEntity<DeleteResponse<?>>(
					deleteResponse, HttpStatus.valueOf(statusCode));
		} else {
			// Unrecognized Request
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}
}
