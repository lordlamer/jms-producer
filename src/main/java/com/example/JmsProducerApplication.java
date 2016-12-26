package com.example;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class JmsProducerApplication implements CommandLineRunner {
	
	@Autowired
	RabbitTemplate rabbitTemplate;
	
	@Value("${demo.queue-name}")
	private String queueName;
	
	@Value("${demo.queue-json-name}")
	private String queueJsonName;
	
	@Bean
	Queue queue() {
		return new Queue(queueName, false);
	}
	
	@Bean
	Queue jsonQueue() {
		return new Queue(queueJsonName, false);
	}

	public static void main(String[] args) {
		SpringApplication.run(JmsProducerApplication.class, args);
	}

	@Override
	public void run(String... arg0) throws Exception {
        //System.out.println("Waiting five seconds...");
        //Thread.sleep(5000);
		for(int i=0; i<20; i++) {
			System.out.println("Sending message...("+queueName+")");
			rabbitTemplate.convertAndSend(queueName, "Hello - " + new Date().toString());
		}
        //Thread.sleep(5000);	
		
		// json test
		ObjectMapper mapper = new ObjectMapper();
		
		SimpleObject a = new SimpleObject();
		a.setAge(11);
		a.setDate(new Date());
		a.setEmail("foo@bar.de");
		a.setName("foo bar #{'\\%%$%&/&%&§$§$!'}#");
		a.setPrice(new BigDecimal(10.123456).setScale(6, BigDecimal.ROUND_HALF_UP));
		
		String objectAsString = mapper.writeValueAsString(a);
		System.out.println("Sending message...("+queueJsonName+")");
		rabbitTemplate.convertAndSend(queueName, "JSON - " + new Date().toString());
		rabbitTemplate.convertAndSend(queueJsonName, objectAsString);
	}
}
