package proPets.messaging;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@SpringBootApplication
public class MessagingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessagingServiceApplication.class, args);
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration configuration = new CorsConfiguration();
	    configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
	    configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
	    configuration.setExposedHeaders(Arrays.asList("Authorization", "content-type"));
	    configuration.setAllowedHeaders(Arrays.asList("Authorization", "content-type"));
	    configuration.setAllowedHeaders(Arrays.asList("X-token", "content-type"));
	    configuration.setAllowedHeaders(Arrays.asList("X-id", "content-type"));
	    configuration.setExposedHeaders(Arrays.asList("X-token", "content-type"));
	    configuration.setExposedHeaders(Arrays.asList("X-id", "content-type"));
	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", configuration);
	    return source;
	}
}
