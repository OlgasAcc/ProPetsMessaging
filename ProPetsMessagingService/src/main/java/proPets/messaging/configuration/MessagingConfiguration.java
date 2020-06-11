package proPets.messaging.configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.annotation.ManagedResource;

import proPets.messaging.model.Post;

@Configuration
@ManagedResource
public class MessagingConfiguration {

	Map<String, Post> users = new ConcurrentHashMap<>();

	/*
	 * public boolean addUser(String sessionId, Post userAccount) { return
	 * users.put(sessionId, userAccount) == null; }
	 * 
	 * public Post getUser(String sessionId) { return users.get(sessionId); }
	 * 
	 * public String getUserLogin(String sessionId) { return
	 * users.get(sessionId).getEmail(); }
	 * 
	 * public Post removeUser(String sessionId) { return users.remove(sessionId); }
	 */


}