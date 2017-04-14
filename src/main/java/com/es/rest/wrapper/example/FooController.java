package com.es.rest.wrapper.example;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.es.rest.wrapper.enumeration.UrlComponentEnum;
import com.es.rest.wrapper.model.generic.DeleteResponse;
import com.es.rest.wrapper.model.generic.IndexResponse;
import com.es.rest.wrapper.model.generic.QueryResponse;

/**
 * Example REST controller for Foo Elasticsearch documents
 * 
 * @author Jack Phillips
 */
@RestController
@RequestMapping("/foo")
public class FooController {

	@Autowired
	private FooService fooService;
	
	/**
	 * Method retrieves all Foo objects, this does the same thing as the
	 * getAllFooWithQueryResponse() method.
	 * 
	 * @return ResponseEntity
	 * @throws IOException
	 */
	@GetMapping(value = "/getAll")
	public ResponseEntity<?> getAllFoo() throws IOException {
		return fooService.getSyncResponseEntity(Foo.class, UrlComponentEnum.SEARCH.getValue());
	}
	
	/**
	 * Method retrieves all Foo objects, this does the same thing as the
	 * getAllFoo() method.
	 * 
	 * @return ResponseEntity
	 */
	@GetMapping(value = "/getAllQueryResp")
	public ResponseEntity<?> getAllFooWithQueryResponse() throws IOException {
		QueryResponse<?> queryResponse = fooService.getAll();
		
		return new ResponseEntity<QueryResponse<?>>(queryResponse, HttpStatus.OK);
	}
	
	/**
	 * Method retrieves a list of Objects contained in the _source portion of the 
	 * Elasticsearch response.
	 * 
	 * @return ResponseEntity with a body containing a list of Foo objects
	 */
	@GetMapping(value = "/getAllObj")
	public ResponseEntity<?> getAllFooObj() {
		List<Object> response = fooService.getAllFoo();

		return new ResponseEntity<List<Object>>(response, HttpStatus.OK);
	}
	
	/**
	 * Retrieves and passes back Foo object in ResponseEntity body.
	 * 
	 * @param id - Document ID
	 * @return ResponseEntity
	 */
	@GetMapping(value = "/findOne/{id}")
	public ResponseEntity<?> findOneFoo(@PathVariable("id") String id) {
		Foo result = fooService.findOneFoo(id);

		return Objects.isNull(result) ? 
				new ResponseEntity<String>("Failed to find object", HttpStatus.BAD_REQUEST) 
				: new ResponseEntity<Foo>(result, HttpStatus.FOUND);
	}
	
	/**
	 * Pulls path variable query params and executes Elasticsearch query
	 * 
	 * @param searchParams (ex. _search)
	 * @return ResponseEntity
	 */
	@GetMapping(value = "/search/{searchParams}")
	public ResponseEntity<?> getFooSearch(@PathVariable("searchParams") String searchParams) {
		QueryResponse<?> queryResponse = fooService.performSyncQuery(searchParams);
		
		return new ResponseEntity<QueryResponse<?>>(queryResponse, HttpStatus.OK);
	}
	
	/**
	 * Get the number of Foo documents in Elasticsearch without using the indexName parameter
	 * 
	 * @return Long count of Foo documents
	 * @throws IOException
	 */
	@GetMapping(value = "/count")
	public Long getFooCount() throws IOException {
		return fooService.getIndexCount(Foo.class);
	}
	
	/**
	 * Get the number of Foo documents in Elasticsearch using the indexName parameter
	 * 
	 * @return Long count of Foo documents
	 * @throws IOException
	 */
	@GetMapping(value = "/count/{indexName}")
	public Long getFooCount(@PathVariable("indexName") String indexName) throws IOException {
		return fooService.getIndexCount(indexName);
	}
	
	/**
	 * Creates new Foo object in Elasticsearch, synonymous with postFooWithIndexResponse method
	 * 
	 * @param foo
	 * @return ResponseEntity with IndexResponse wrapped in body
	 * @throws IOException
	 */
	@PostMapping(value = "/create", consumes = "application/json")
	public ResponseEntity<?> postFoo(@RequestBody Foo foo) throws IOException {
		return fooService.postSyncResponseEntity(foo, null);
	}
	
	/**
	 * Creates new Foo object in Elasticsearch, synonymous with postFoo method
	 * 
	 * @param foo
	 * @return ResponseEntity with IndexResponse wrapped in body
	 * @throws IOException
	 */
	@PostMapping(value = "/createIndexResp", consumes = "application/json")
	public ResponseEntity<?> postFooWithIndexResponse(@RequestBody Foo foo) throws IOException {
		IndexResponse<?> indexResponse = fooService.peformFooPost(foo);
		
		return new ResponseEntity<IndexResponse<?>>(indexResponse, HttpStatus.OK);
	}
	
	/**
	 * Updates document in ElasticSearch
	 * 
	 * @param foo
	 * @param id - Document ID
	 * @return ResponseEntity
	 * @throws IOException
	 */
	@PostMapping(value = "update/{id}")
	public ResponseEntity<?> updateFoo(@RequestBody Foo foo, @PathVariable("id") String id) throws IOException {
		return fooService.postSyncResponseEntity(foo, id);
	}

	/**
	 * Deletes document from Elastisearch with DeleteResponse in response body
	 * synonymous with deleteFooByIdWithDeleteResponse method
	 * 
	 * @param id
	 * @return ResponseEntity
	 * @throws IOException
	 */
	@DeleteMapping(value = "/remove/{id}")
	public ResponseEntity<?> deleteFooById(@PathVariable("id") String id) throws IOException {
		return fooService.deleteSyncResponseEntity(Foo.class, id);
	}
	
	/**
	 * Deletes document from Elastisearch with DeleteResponse in response body
	 * synonymous with deleteFooById method
	 * 
	 * @param id
	 * @return
	 * @throws IOException
	 */
	@DeleteMapping(value = "/removeDeleteResp/{id}")
	public ResponseEntity<?> deleteFooByIdWithDeleteResponse(@PathVariable("id") String id) throws IOException {
		DeleteResponse<?> deleteResponse = fooService.deleteFoo(id);
		
		return new ResponseEntity<DeleteResponse<?>>(deleteResponse, HttpStatus.ACCEPTED);
	}
	
	
}
