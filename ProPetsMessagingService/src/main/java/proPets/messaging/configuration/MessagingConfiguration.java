package proPets.messaging.configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import proPets.messaging.model.Post;

@Configuration
@RefreshScope
public class MessagingConfiguration {

	Map<String, Post> posts = new ConcurrentHashMap<>();
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	@Value("${message.quantity}")
	int quantity;
	
	@RefreshScope
	public int getQuantity() {
		return quantity;
	}

}