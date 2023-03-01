package com.example.customer.controller;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.customer.dto.Account;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AccountInfoConsumer {
	
	@KafkaListener(topics = { "accInfo" }, groupId = "spring-boot-consumer-id")
	public void consume(ConsumerRecord<String, GenericRecord> record) {
		System.out.println("received = " + record.value() + " with key " + record.key());
		Account accDTO = null;
		String accINFO = record.value().toString();
		String access_token = record.key();
		
		System.out.println("Consumed message >>> "+accINFO);

		ObjectMapper mapper = new ObjectMapper();
		try {
			accDTO = mapper.readValue(accINFO, Account.class);
		
			//adminService.sendInfoToAllServices(userDTO);

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

}

