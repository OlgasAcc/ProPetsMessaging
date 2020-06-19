package proPets.messaging.dao;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.mongodb.repository.MongoRepository;

import proPets.messaging.model.Post;

public interface MessagingRepository extends MongoRepository<Post, String> {

	List<Post> findAll();
	
	Stream<Post>findByAuthorId(String authorId);
	
}
