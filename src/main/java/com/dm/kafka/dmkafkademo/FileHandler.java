package com.dm.kafka.dmkafkademo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import com.albertsons.model.Customer;
import com.google.gson.Gson;


public class FileHandler implements MessageHandler {
	
	private String outputLocation;
	
	@Autowired
	private com.dm.kafka.dmkafkademo.DmKafkaDemoApplication.MessageProducer messageProducer;

	
	//private  kafkaProducer producer;

	public FileHandler(String outputLocation) {
		this.outputLocation = outputLocation;
		
	}
   
	public FileHandler(com.dm.kafka.dmkafkademo.DmKafkaDemoApplication.MessageProducer MessageProducer) {
		this.messageProducer = messageProducer;
		
	}
   
	@Override
	public void handleMessage(Message<?> message) throws MessagingException {
		// TODO Auto-generated method stub
		File file = (File) message.getPayload();
		//message.getPayload();
		Gson gson = new Gson();
		//Reader reader ;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			// reader = Files.newBufferedReader(Paths.get("/Users/jpaulraj/albertson/in/customer.json"));
			 Customer user = gson.fromJson(reader,Customer.class);
			 String json = gson.toJson(user);
				//producer.sendMessage(json);
			 messageProducer.sendMessage(json);
			    // print user object
			    System.out.println(json);

			    // close reader
			    reader.close();

			 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    // convert JSON string to User object
	   // User user = gson.fromJson(reader,User.class);


		
		System.out.println("File received " + file.getAbsolutePath());
		moveFile(file);

	}
	
	
	private void moveFile(File file) {
		try {
			Date dt = new Date();
			FileUtils.moveFileToDirectory(FileUtils.getFile(file), FileUtils.getFile("/Users/jpaulraj/albertson/processed/"+file.getName()+dt.toString()), true);
			System.out.println("File moved successfully to /Users/jpaulraj/albertson/processed/");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] a) {
		
		Gson gson = new Gson();
		
		
		Customer customer = new Customer();
		customer.setAddress("3018 winter park palce");
		customer.setName("Jey");
		customer.setCity("Madison");
		
	    // create a writerß
	    Writer writer;
		try {
			writer = Files.newBufferedWriter(Paths.get("/Users/jpaulraj/albertson/in/customer.json"));
			gson.toJson(customer, writer);

		    // close writer
		    writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    // convert book object to JSON file
	    


		
	}


}
