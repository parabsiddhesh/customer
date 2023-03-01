package com.example.customer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableAsync
public class ExtServiceConfig {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExtServiceConfig.class);

	
	//@LoadBalanced
	@Bean
	public WebClient getWebClient() {
		return WebClient.builder().baseUrl("http://localhost:8081/account")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
	}
	
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
