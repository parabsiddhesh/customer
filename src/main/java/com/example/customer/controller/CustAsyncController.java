package com.example.customer.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.customer.dto.Account;
import com.example.customer.service.CustomerService;

@RestController
@RequestMapping("/customer")
public class CustAsyncController {

	@Autowired
	CustomerService customerService;

	@PostMapping("/custasync")
	public Map<String, Boolean> verifyAdminervice(@RequestBody Account accInfo) {
		Map<String, Boolean> map = new HashMap<String, Boolean>();

		try {
			customerService.sendInfoToAllServices(accInfo);
			map.put("status", true);
		} catch (Exception e) {
			map.put("status", false);
			e.printStackTrace();
		}
		return map;
	}
	
	@GetMapping("/debitamt")
    //@CircuitBreaker(name = SERVICE_A, fallbackMethod = "serviceAFallback")
    //@Retry(name = SERVICE_A)
    //@RateLimiter(name = SERVICE_A)
	public String debitAmount() {
		return customerService.callExtmenthoda();
	}

}
