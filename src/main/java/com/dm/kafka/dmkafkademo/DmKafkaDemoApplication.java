package com.dm.kafka.dmkafkademo;

import java.io.File;

import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.file.filters.LastModifiedFileListFilter;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.MessageHandler;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;


@SpringBootApplication
public class DmKafkaDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DmKafkaDemoApplication.class, args);
	}
	
	@Bean
    public KieSession kieSession() {
		final KieServices kieServices = KieServices.Factory.get();
	    final ReleaseId releaseId = kieServices.newReleaseId("com.albertsons", "dataquality", "LATEST");
	    final KieContainer kieContainer = kieServices.newKieContainer(releaseId);
	    final KieSession kieSession = kieContainer.newKieSession();

	    final KieScanner kieScanner = kieServices.newKieScanner(kieContainer);
	    kieScanner.start(10000);
	    
	    //log.warn(":) created a KIE Container - returning...");
	    return kieSession;
}

	
	@Bean
    public MessageProducer messageProducer() {
        return new MessageProducer();
    }

  
    
	public static class MessageProducer {
		
		 @Autowired
	     private KafkaTemplate<String, String> kafkatemplate;
		 
		 @Value(value = "${message.topic.name}")
	        private String topicName;
		 
		 public void sendMessage(String message) {

	            ListenableFuture<SendResult<String, String>> future = kafkatemplate.send(topicName, message);

	            future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

	                @Override
	                public void onSuccess(SendResult<String, String> result) {
	                    System.out.println("Sent message=[" + message + "] with offset=[" + result.getRecordMetadata()
	                        .offset() + "]");
	                }

	                @Override
	                public void onFailure(Throwable ex) {
	                    System.out.println("Unable to send message=[" + message + "] due to : " + ex.getMessage());
	                }
	            });
	        }
	
	}
	 /* @Bean
    public MessageListener messageListener() {
        return new MessageListener();
    }
	public static class MessageListener {
        private CountDownLatch latch = new CountDownLatch(3);

		
		 @KafkaListener(topics = "${message.topic.name}", groupId = "foo", containerFactory = "fooKafkaListenerContainerFactory")
	        public void listenGroupFoo(String message) {
	            System.out.println("Received Messasge in group 'foo': " + message);
	            latch.countDown();
	        }

	}*/
	@Bean
	@InboundChannelAdapter(value = "fileChannel", poller = @Poller(fixedDelay = "1000"))
	public MessageSource<File> fileReadingMessageSource() {
	    CompositeFileListFilter<File> filters = new CompositeFileListFilter<>();
	    filters.addFilter(new SimplePatternFileListFilter("*.json"));
	    filters.addFilter(new LastModifiedFileListFilter());
	    
	    FileReadingMessageSource source = new FileReadingMessageSource();
	    source.setAutoCreateDirectory(true);
	    source.setDirectory(new File("/Users/jpaulraj/albertson/in/"));
	    source.setFilter(filters);
	    
	    return source;
	}

	@Bean
	@ServiceActivator(inputChannel = "fileChannel")
	
	public MessageHandler fileWritingMessageHandler() {
		return new FileHandler( messageProducer());
	}
	
	/*	
	@Bean
    public KieContainer kieContainer() {
		
		final KieServices kieServices = KieServices.Factory.get();
		 final ReleaseId releaseId = kieServices.newReleaseId("com.albertsons", "dataquality", "latest");
		final KieContainer kieContainer = kieServices.newKieContainer(releaseId);		
	    return kieContainer;
}*/
/*	@Bean
	public ConcurrentKafkaListenerContainerFactory kafkaListenerContainerFactory(
	    ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
	    ConsumerFactory<Object, Object> kafkaConsumerFactory,
	    KafkaTemplate<Object, Object> template) {
	  ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
	  configurer.configure(factory, kafkaConsumerFactory);
	  factory.setErrorHandler(new SeekToCurrentErrorHandler(
	      new DeadLetterPublishingRecoverer(template), 3));
	  return factory;
	}*/
	
	

}
