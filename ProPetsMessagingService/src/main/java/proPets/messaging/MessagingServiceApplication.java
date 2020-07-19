package proPets.messaging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MessagingServiceApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(MessagingServiceApplication.class, args);
	}

}
