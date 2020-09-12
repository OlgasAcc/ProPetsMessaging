package proPets.messaging.dao;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import proPets.messaging.model.Post;

public interface MessagingRepository extends MongoRepository<Post, String>
 {
	
	@Query("{ \"$and\" : [{ \"?0\" : { \"$nin\" : [\"usersHidThisPost\"]}}, { \"?0\" : { \"$nin\" : [\"usersUnfollowedThisPostByAuthor\"]}}]}")
	Page<Post> findAll(String currentUserId, Pageable pageReq);
	
	List<Post> findByUsersAddedThisPostToFavoritesAuthorId(String authorId);
	
	Page<Post> findByUsersAddedThisPostToFavoritesAuthorId(String authorId, Pageable pageReq);
	
	Stream<Post>findByAuthorDataAuthorId(String authorId);
	
}
