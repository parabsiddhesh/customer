package com.example.customer.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.customer.dto.Account;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import reactor.core.publisher.Mono;

@Service
public class ExternalServiceCalls {
	private static final Logger logger = LoggerFactory.getLogger(ExternalServiceCalls.class);

	@Autowired
	@Lazy
	private RestTemplate restTemplate;

	@Autowired
	CreditServiceClient creditServiceClient;

	@Autowired
	WebClient webClient;

	@Value("${debit.service.uri}")
	public String debitURL;

	public CompletableFuture<Map<String, Boolean>> callCreditService(Account accInfo) throws InterruptedException {
		CompletableFuture<Map<String, Boolean>> future = CompletableFuture
				.supplyAsync(new Supplier<Map<String, Boolean>>() {
					@Override
					public Map<String, Boolean> get() {
						logger.info(" Thread " + Thread.currentThread().getName());

						Map<String, Boolean> result = creditServiceClient
								.callCreditService(accInfo);
						return result;
					}
				}).handle((res, ex) -> {
					if (ex != null) {
						System.out.println("Oops! We have an exception - " + ex.getMessage());
						Map<String, Boolean> map = new HashMap<String, Boolean>();

						map.put("status", false);
						return map;
					}
					return res;
				});

		return future;

	}

	@CircuitBreaker(name = "customerService", fallbackMethod = "verifyDebitServiceFallback")
	public CompletableFuture<Map<String, Boolean>> callDebitService(Account accInfo) throws InterruptedException {
		CompletableFuture<Map<String, Boolean>> future = null;
		try {
			future = CompletableFuture.supplyAsync(new Supplier<Map<String, Boolean>>() {

				@Override
				public Map<String, Boolean> get() {
					// logger.info("Thread " + Thread.currentThread().getName());
					HttpHeaders headers = new HttpHeaders();

					HttpEntity request = new HttpEntity(accInfo, headers);
					return restTemplate.postForObject(debitURL, request, Map.class);
				}
			});
		} catch (Exception e) {
			System.out.println("Oops! We have an exception - " + e.getMessage());
		}
		return future;
		
	}

	public CompletableFuture<Account> callAccountSer(Account accInfo) throws InterruptedException {
		CompletableFuture<Account> future = CompletableFuture.supplyAsync(new Supplier<Account>() {
			@Override
			public Account get() {
				Mono<Account> bodyToMono = webClient.post().uri("/create")
						.body(Mono.just(accInfo), Account.class)
						.retrieve()
						.bodyToMono(Account.class);
				return bodyToMono.block();
			}
		});
		return future;
	}

	
	@CircuitBreaker(name = "customerService", fallbackMethod = "verifyDebitFallback")
	public Map<String, Boolean> restTemplateCallToDebitService(Account accInfo) {

		return restTemplate.postForObject(debitURL, accInfo, Map.class);

	}

	public CompletableFuture<Map<String, Boolean>> verifyDebitServiceFallback(Account accInfo, Exception e) {
		logger.info(" ############# CompletableFuture fallback method is called Exception " + e.getMessage()
				+ "###################");
		Map<String, Boolean> map = new HashMap<String, Boolean>();

		map.put("status", false);
		return CompletableFuture.completedFuture(map);

	}

	public Map<String, Boolean> verifyDebitFallback(Account accInfo, Exception e) {
		logger.info("fallback method is called");
		Map<String, Boolean> map = new HashMap<String, Boolean>();

		map.put("status", true);
		return map;
	}
	
	//@CircuitBreaker(name = "debitMenthodA", fallbackMethod = "debitMenthodAFall")
	@Retry(name = "debitMenthodA",  fallbackMethod = "debitMenthodAFall")
	//@RateLimiter(name = "debitMenthodB", fallbackMethod = "debitMenthodAFall")
	public String debitMenthodA() {
		return restTemplate.getForObject(debitURL+"/b", String.class);
	}
	
	public String debitMenthodAFall(Exception e) {
		logger.info("fallback method is called");
		return "Amount not debited";
	}
}
