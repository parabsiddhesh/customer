package com.example.customer.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.customer.dto.Account;

//@FeignClient(name ="http://localhost:8083/credit")
@FeignClient(value = "CREDIT-SERVICE", url = "https://credit-git-parabsiddhesh-dev.apps.sandbox-m3.1530.p1.openshiftapps.com/credit")
public interface CreditServiceClient {

	@PostMapping
	Map<String, Boolean> callCreditService(@RequestBody Account accInfo);

}
