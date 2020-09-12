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
@Setter
@EqualsAndHashCode(of = { "id" })
@Builder

@Document(collection = "all_posts")

public class Post {
	@Id
	String id;
	String text;
	Set<String> pictures;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	LocalDateTime dateOfPublish;

	AuthorData authorData;
	Set<String> usersHidThisPost; // set of User's ids who made this post hidden from their feed
	Set<AuthorData> usersAddedThisPostToFavorites; 
	Set<String> usersUnfollowedThisPostByAuthor;

	public Post(String text, Set<String> pictures) {
		this.text = text;
		authorData = new AuthorData();
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

	public boolean addToUsersThatHidThisPost(String userId) {
		return usersHidThisPost.add(userId);
	}
	
	public boolean addToUsersThatUnfollowedThisPostByAuthor(String userId) {
		return usersHidThisPost.add(userId);
	}

	public boolean removeFromUserThatAddedThisPostToFav(String userId) {
		for (AuthorData ad : usersAddedThisPostToFavorites) {
			if (ad.getAuthorId().equalsIgnoreCase(userId)) {
				usersAddedThisPostToFavorites.remove(ad);
				return true;
			}
		}
		return false;
	}
	
	public boolean addToUserThatAddedThisPostToFav(String userId) {
		AuthorData authorData = AuthorData.builder()
				.authorId(userId)
				.authorName("none")
				.authorAvatar("none")
				.build();
		return usersAddedThisPostToFavorites.add(authorData);
	}

}
