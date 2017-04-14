package com.es.rest.wrapper;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.sniff.Sniffer;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfiguration {	
	@Value("${es.auth.basic}")
	private Boolean useBasicAuth;
	
	@Value("${elasticsearch.host}")
	private String host;

	@Value("${elasticsearch.port}")
	private int port;
	
	@Value("${elasticsearch.cluster}")
	private String cluster;
	
	@Value("${elasticsearch.xpack.credentials}")
	private String xpackCredentials;
	
	@Value("${es.rest.port}")
	private int restPort;
	
	@Value("${es.auth.user}")
	private String authUser;
	
	@Value("${es.auth.passwd}")
	private String authPass;
	
	private final String CLUSTER_KEY = "cluster.name";
	private final String XPACK_USER_KEY = "xpack.security.user";
	private final String HTTP_SCHEME = "http";
	
	private Sniffer sniffer;
	
//	@Bean(destroyMethod = "close")
	public Client client() throws UnknownHostException {
		
		@SuppressWarnings("resource")
		TransportClient client = new PreBuiltXPackTransportClient(buildBasicAuthSettings())
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
		
		return client;
	}
	
	@Bean(destroyMethod = "close")
	public RestClient restClient() {
		RestClient restClient = useBasicAuth ? buildBasicAuthRestClient() : buildAdvancedAuthRestClient();
		this.sniffer = Sniffer.builder(restClient).build();
		
		return restClient;
	}
	
	private RestClient buildBasicAuthRestClient() {
		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(
				AuthScope.ANY, 
				new UsernamePasswordCredentials(authUser, authPass));
		
		RestClient restClient = RestClient.builder(createHttpHost())
				.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
					@Override
					public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
						return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
					}
				})
				.build();
		
		return restClient;
	}
	
	private RestClient buildAdvancedAuthRestClient() {
		// TODO: Implement certificate authentication/other security measure
		return null;
	}
	
	private HttpHost createHttpHost() {
		return new HttpHost(host, restPort, HTTP_SCHEME);
	}
	
	private Settings buildBasicAuthSettings() {
		Settings settings = Settings.builder()
				.put(CLUSTER_KEY, cluster)
				.put(XPACK_USER_KEY, xpackCredentials)
				.build();
		return settings;
	}
	
}
