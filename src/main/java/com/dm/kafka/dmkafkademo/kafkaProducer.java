package com.dm.kafka.dmkafkademo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class kafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(kafkaProducer.class);
    private static final String TOPIC = "test";

    @Autowired
	private  KafkaTemplate<String, String> kafkaTemplate;

    /*@Autowired
    public kafkaProducer(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }*/
    
    public void sendMessage(String message) {
        logger.info(String.format("#### -> Producing message -> %s", message));
        this.kafkaTemplate.send(TOPIC, message);
    }
}
