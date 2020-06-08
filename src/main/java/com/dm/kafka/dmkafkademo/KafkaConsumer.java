package com.dm.kafka.dmkafkademo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.albertsons.model.Customer;
import com.google.gson.Gson;
@Service
public class KafkaConsumer {
	
	private final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
	
	@Autowired
	private  KieSession kieSession;

    @KafkaListener(topics = "test", groupId = "group_id")
    public void consume(String message) throws IOException {
        logger.info(String.format("#### -> Consumed message -> %s", message));
       
        Gson gson = new Gson();
        Customer customer = gson.fromJson(message, Customer.class);
        
        //INSET FACT TO ENGINE
        kieSession.insert(customer);
        kieSession.fireAllRules();
        
        logger.info(String.format("#### ->customer county -> %s", customer.getCounty()));
        
        createJsonFile(customer);
        
        
    }
    public void createJsonFile(Customer result) {
        
    	Gson gson = new Gson();

    	String jsonstring = gson.toJson(result);
    	logger.info(String.format("#### -> after rule executed -> %s", jsonstring));
    	Date dt = new Date();
    	
    	String fileName = "/Users/jpaulraj/albertson/out/"+dt.toString();
    	 File file = new File(fileName);
         
         FileWriter fw;
		try {
			fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
	         bw.write(jsonstring);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
    	
    }

}
