### Elasticsearch REST Java Wrapper
**Issue**: Currently, there is no Spring data implementation for Elasticsearch releases past 2.x. This is due to changes within the *TransportClient* and REST library introduced in Elasticsearch 5.x that haven’t been implemented yet. Also, AWS **only** supports **HTTP** communication so use of the typical TransportClient TCP protocol cannot be used.

**Solution**: Create REST client wrapper that can give developers the tools to interact with Elasticsearch quickly and provide typical REST functions.

#### Key Components
 - Utilize Jackson to map JSON to Java objects and vice versa.
 - Use annotations at the class (type) level to declare a Java object as an Elasticsearch document
 - Swagger UI provides easy execution of REST endpoints.
 - Elasticsearch must use *log4j* or some wrapper to execute without errors.
#### How-to
*Take a look at the **com.es.rest.wrapper.example** package located in this repository. It contains examples of creating a POJO annoted with the @ElasticDocument, service and controller classes.* 

 - Download and install Elasticsearch locally, if using authentication you must also install X-Pack.
 - Import Gradle project into your IDE of choice “workspace”.
 - Locate the application.properties file and update the values as you see fit, these properties control the Swagger interface, what port the server will run on, and Elasticsearch connection information (host, port, etc.). 
 - Create a POJO and annotate it with @ElasticDocument then specify the index and type (if it exists) that is used in Elasticsearch.
````
@ElasticDocument(indexName = "foo", type = "bar")
public class Foo {
    private String value;
    public Foo(){} // Need no arg constructor for Jackson
    // Getters and Setters
}
````
 - Create a Service class and **extend ElasticsearchService**.
````
@Service
public class FooService extends ElasticsearchService {
    // Override or implement logic not encompassed within   ElasticsearchService
}
````
 - Create a controller class to handle the REST endpoints and pass along calls to the service class.
````
@RestController
@RequestMapping("/foo")
public class FooController {
    @Autowired
    private FooService fooService;
}
````
 - Out of the box you can perform typical REST operations that will return either the POJO from the response or include all the meta-data information corresponding to an Elasticsearch REST call. The mapping of these requests are as follows:
	- **GET** = QueryResponse, POJO or Long if getting the number of documents in a particular index.
	- **POST/PUT** = IndexResponse
	- **DELETE** = DeleteResponse
 - Although you can return these response classes there is also methods to return a *ResponseEntity* that contains the proper HTTP status code and response in the body of the entity. 
	 - *NOTE: currently this is only used with responses that include the Elasticsearch meta-data.*

#### References
- [Elasticsearch Documentation](https://www.elastic.co/guide/en/elasticsearch/reference/5.2/index.html)
- [X-Pack](https://www.elastic.co/products/x-pack/security)
- [Swagger URL - http://localhost:8089/swagger-ui.html#](http://localhost:8089/swagger-ui.html#)
- [Sense Chrome Plugin - Elasticsearch JSON developer console]( https://chrome.google.com/webstore/detail/sense-beta/lhjgkmllcaadmopgmanpapmpjgmfcfig)
