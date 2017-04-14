package com.es.rest.wrapper.example;

import java.io.IOException;
import java.util.List;

import org.apache.http.ParseException;
import org.springframework.stereotype.Service;

import com.es.rest.wrapper.model.generic.DeleteResponse;
import com.es.rest.wrapper.model.generic.IndexResponse;
import com.es.rest.wrapper.model.generic.QueryResponse;
import com.es.rest.wrapper.service.ElasticsearchService;

@Service
public class FooService extends ElasticsearchService {

	public QueryResponse<?> performAsyncQuery(String query) {
		return performAsyncRequest(Foo.class, query);
	}
	
	public QueryResponse<?> performSyncQuery(String query) {
		try {
			return getSync(Foo.class, query);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public QueryResponse<?> getAll() throws ParseException, IOException {
		return getAllSync(Foo.class);
	}
	
	public List<Object> getAllFoo() {
		try {
			return findAll(Foo.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public IndexResponse<?> peformFooPost(Foo fooObj) {
		try {
			return (IndexResponse<?>) postSync(fooObj);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public IndexResponse<?> peformFooUpdate(Foo fooObj, String id) {
		try {
			return (IndexResponse<?>) postSync(fooObj, id);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public DeleteResponse<?> deleteFoo(String id) {
		try {
			return deleteSyncById(id, Foo.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Foo findOneFoo(String id) {
		try {
			return (Foo) findOne(Foo.class, id);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
