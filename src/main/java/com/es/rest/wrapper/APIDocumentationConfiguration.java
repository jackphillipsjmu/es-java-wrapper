package com.es.rest.wrapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
/**
 * <p>Configures the automatic generation of Swagger v2 API documentation for all endpoint exposed by this application.</p>
 * 
 * <p>This configuration exposes the following API documentation endpoints:</p>
 * <ul>
 * 	<li>Swagger UI - /swagger-ui.html</li>
 * 	<li>Swagger API JSON - /v2/api-docs</li>
 * </ul>
 */
@Configuration
@EnableSwagger2
public class APIDocumentationConfiguration {
	/* Application Info Properties */
	@Value("${info.app.name}")
	private String appName;

	@Value("${info.app.description}")
	private String appDescription;

	@Value("${info.app.version}")
	private String appVersion;

	@Value("${info.app.contact.name}")
	private String contactName;

	@Value("${info.app.contact.url}")
	private String contactUrl;

	@Value("${info.app.contact.email}")
	private String contactEmail;
	
	/**
	 * Configure the swagger documentation generation
	 * @return Docket
	 */
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
			.select()
			.apis(RequestHandlerSelectors.any())
			.paths(PathSelectors.any())
				.build().apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		Contact contact = new Contact(contactName, contactUrl, contactEmail);

		ApiInfo info = new ApiInfo(appName, appDescription, appVersion, "",
				contact, "", "");
		return info;
	}
}
