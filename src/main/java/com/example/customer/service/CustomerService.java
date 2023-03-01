package com.example.customer.service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.customer.dto.Account;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class CustomerService {

	@Autowired
	ExternalServiceCalls exServiceCalls;

	public void sendInfoToAllServices(Account accDto) {
		try {
			long start = System.currentTimeMillis();
			CompletableFuture<Map<String, Boolean>> creditServiceResult = sendUserInfoTOCreditService(accDto);
			CompletableFuture<Map<String, Boolean>> debitServiceResult = sendUserInfoTODebitService(accDto);
			CompletableFuture<Account> accountDTO = sendDtatoAccount(accDto);
			CompletableFuture.allOf(debitServiceResult, creditServiceResult, accountDTO).thenAccept(r -> {
				try {
					Optional<Map<String, Boolean>> dResult = Optional.ofNullable(debitServiceResult.get());
					Optional<Map<String, Boolean>> cRestlt = Optional.ofNullable(creditServiceResult.get());
					Optional<Account> accDTO = Optional.ofNullable(accountDTO.get());
					if (dResult.isPresent() && cRestlt.isPresent()) {
						System.out.println(
								" Both Credit and Debit Services are verified Sucessfully >>>>>>>>>>>>>> ");
						System.out.println(" CreditService Status >>>>>>>>>>>>>> "
								+ cRestlt.get().get("status"));
						System.out.println(" DebitService Status >>>>>>>>>>>>>> "
								+ dResult.get().get("status"));
					}
					long end = System.currentTimeMillis();
					System.out.println(" StartTime and EndTime >>>>>>>>>>>>>> " + (end - start));
					System.out.println(" Account service Info >>>>>>>>>>>>>> " + accDTO.get());

				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}).join();

			CompletableFuture<Void> cpFuture = logRsunAsyn();
			cpFuture.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	public CompletableFuture<Map<String, Boolean>> sendUserInfoTOCreditService(Account accInfo) {

		try {
			return exServiceCalls.callCreditService(accInfo);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;

	}

	public CompletableFuture<Map<String, Boolean>> sendUserInfoTODebitService(Account accInfo) {
		try {
			return exServiceCalls.callDebitService(accInfo);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;

	}

	public CompletableFuture<Account> sendDtatoAccount(Account accInfo) {
		try {
			return exServiceCalls.callAccountSer(accInfo);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public CompletableFuture<Void> logRsunAsyn() {
		CompletableFuture<Void> runSynFurture = CompletableFuture
				.runAsync(() -> System.out.println(" ############Logging in RunAsync mode #########"));

		return runSynFurture;

	}
	
	public String callExtmenthoda() {
		return exServiceCalls.debitMenthodA();
	}
	
}
