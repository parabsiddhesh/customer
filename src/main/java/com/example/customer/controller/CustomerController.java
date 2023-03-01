package com.example.customer.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import com.example.customer.APIClient;
import com.example.customer.dto.Account;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/customer")
public class CustomerController {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private WebClient webClient;
	
	@Autowired
	private APIClient apiClient;

	@GetMapping(value = "/getAllAcc")
	public List<Account> getAccountList() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<String>(headers);

		return restTemplate.exchange("http://localhost:8081/account/getAllaccounts", HttpMethod.GET, entity, List.class)
				.getBody();
	}
	
	@GetMapping("/{id}")
    public Mono <Account> getAccount(@PathVariable String id) {

        return webClient.get()
            .uri("/account/{id}", id)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .retrieve()
            .bodyToMono(Account.class);
    }
	
	@GetMapping("/cust/{id}")
    public Account getAccbyid(@PathVariable long id) {
        return apiClient.getAccountById(id);
    }
	
	@GetMapping(value = "/cust1/update/{id}")
	public String updateAccount(@PathVariable long id,@RequestBody Account accInfo) {
		try {
			Map < String, Long > params = new HashMap();
	        params.put("id", id);
	        restTemplate.put("http://localhost:8081/account/{id}", accInfo, params);
			return "Acoount updated using rest successfully";
		} catch (Exception e) {
			// TODO: handle exception
			return "Account updation using rest failed";
		}
	}
	
	@GetMapping(value = "/cust1/delete/{id}")
	public String deleteAccount(@PathVariable long id) {
		try {
			Map < String, Long > params = new HashMap();
	        params.put("id", id);
	        restTemplate.delete("http://localhost:8081/account/{id}", params);
			return "Acoount deleted using rest successfully";
		} catch (Exception e) {
			// TODO: handle exception
			return "Account deletion using rest  failed";
		}
	}
	
	@GetMapping("/cust2/update/{id}")
	public String update1Account(@PathVariable String id, @RequestBody Account accInfo) {
		String msg = null;
		try {
			Mono<Account> a = webClient.put().uri(id).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
					.body(BodyInserters.fromValue(accInfo)).retrieve().bodyToMono(Account.class);
			if (a != null) {
				msg = "Acoount updated using webclient successfully";
			} else {
				msg = "Acoount updation using webclient failed";
			}
		} catch (Exception e) {
			// TODO: handle exception
			msg = "Acoount updation using webclient failed";
		}
		return msg;
	}
	
	@GetMapping("/cust2/delete/{id}")
	public String delete1Account(@PathVariable String id) {
		String msg = null;
		try {
			 msg = webClient.delete()
					 .uri("/posts/" + id)
					 .retrieve()
					 .toString();
			if (msg != null) {
				msg = "Acoount deleted using webclient successfully";
			} else {
				msg = "Acoount deletion using webclient failed";
			}
		} catch (Exception e) {
			// TODO: handle exception
			msg = "Acoount deletion using webclient failed";
		}
		return msg;
	}
	
	@GetMapping("/cust3/update/{id}")
    public String update2Account(@PathVariable long id, @RequestBody Account accInfo) {
		String msg = null;
		try {
			 Account a1 = apiClient.updateAccount(id,accInfo);
		        if (a1 != null) {
					msg = "Acoount updated using feingclient successfully";
				} else {
					msg = "Acoount updation using feingclient failed";
				}
		} catch (Exception e) {
			msg = "Acoount updation using feingclient failed";
		}
		return msg;
    }

	@GetMapping("/cust3/delete/{id}")
    public String delete2Account(@PathVariable long id) {
		String msg = null;
		try {
			 msg = apiClient.removeAccount(id);
		        if (msg != null) {
					msg = "Acoount deleted using feingclient successfully";
				} else {
					msg = "Acoount deletion using feingclient failed";
				}
		} catch (Exception e) {
			msg = "Acoount deletion using feingclient failed";
		}
		return msg;
    }
	
}
