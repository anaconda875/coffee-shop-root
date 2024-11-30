package org.example.coffeeshop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class CommonConfig {

	private final String shopBaseUri;

	public CommonConfig(@Value( "${app.shop-base-uri}" ) String shopBaseUri) {
		this.shopBaseUri = shopBaseUri;
	}

	@Bean
	RestTemplate orderRestTemplate(ResponseErrorHandler handler) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setUriTemplateHandler( new DefaultUriBuilderFactory( shopBaseUri ) );
		restTemplate.setErrorHandler( handler );

		return restTemplate;
	}

}
