package proPets.messaging.configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.annotation.ManagedResource;

import proPets.messaging.model.Post;

@Configuration
@ManagedResource
public class MessagingConfiguration {

	Map<String, Post> posts = new ConcurrentHashMap<>();

}