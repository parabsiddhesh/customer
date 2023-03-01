package com.example.customer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.customer.dto.Account;

@FeignClient(value = "ACCOUNT-SERVICE", url = "http://localhost:8081")
public interface APIClient {

	@GetMapping(value = "/account/{id}")
	Account getAccountById(@PathVariable("id") Long id);
	
	@PutMapping(value = "/account/{id}")
	Account updateAccount(@PathVariable("id") Long id,@RequestBody Account accinfo);
	
	@DeleteMapping(value = "/account/{id}")
	String removeAccount(@PathVariable("id") Long id);

}
