package proPets.messaging.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode(of = { "id" })
@Builder
@Document(collection = "all_posts")

public class Post {
	@Id
	String id;
	@Setter
	String text;
	@Setter
	Set<String> pictures;
	@Setter
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	LocalDateTime dateOfPublish;
	@Setter
	String authorId;
	String authorAvatar;
	String authorName;
	Set<String> usersHidThisPost; // set of User's ids who made this post hidden from their feed
	Set<String> usersAddedThisPostToFavorites;
	@Setter
	Set<String> usersUnfollowedThisPostByAuthor;

	public Post(String text, String authorId, String authorAvatar, String authorName, Set<String> pictures) {
		this.text = text;
		this.authorId = authorId;
		this.authorAvatar = authorAvatar;
		this.authorName = authorName;
		dateOfPublish = LocalDateTime.now();
		this.pictures = pictures;
		usersHidThisPost = new HashSet<>();
		usersAddedThisPostToFavorites = new HashSet<>();
		usersUnfollowedThisPostByAuthor = new HashSet<>();
	}

	public boolean addPicture(String picture) {
		if (pictures.size() <= 4) {
			return pictures.add(picture);
		} else
			throw new MaxUploadSizeExceededException(4);
	}

	public boolean removePicture(String picture) {
		return pictures.remove(picture);
	}

	public boolean addUserThatHidThisPost(String userId) {
		return usersHidThisPost.add(userId);
	}

	public boolean addUserThatAddedThisPostToFav(String userId) {
		return usersAddedThisPostToFavorites.add(userId);
	}

	public boolean removeUserThatAddedThisPostToFav(String userId) {
		return usersAddedThisPostToFavorites.remove(userId);
	}
	
	public boolean addUserThatUnfollowedThisPostByAuthor(String userId) {
		return usersUnfollowedThisPostByAuthor.add(userId);
	}

}
