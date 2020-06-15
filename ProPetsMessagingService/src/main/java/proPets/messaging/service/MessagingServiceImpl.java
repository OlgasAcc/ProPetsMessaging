package proPets.messaging.service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.AccessException;
import org.springframework.stereotype.Service;

import proPets.messaging.configuration.MessagingConfiguration;
import proPets.messaging.dao.MessagingRepository;
import proPets.messaging.dto.NewPostDto;
import proPets.messaging.dto.PostDto;
import proPets.messaging.dto.PostEditDto;
import proPets.messaging.exceptions.PostNotFoundException;
import proPets.messaging.model.Post;

@Service
public class MessagingServiceImpl implements MessagingService {

	@Autowired
	MessagingRepository messagingRepository;

	@Autowired
	MessagingConfiguration messagingConfiguration;

	@Override
	public PostDto addPost(String currentUserId, NewPostDto newPostDto) {
		Post newPost = new Post(newPostDto.getText(), currentUserId, newPostDto.getAuthorAvatar(), newPostDto.getAuthorName(), newPostDto.getPictures());
		Post postByCurrentUser = messagingRepository.findByAuthorId(currentUserId).findFirst().orElse(null);
		if (postByCurrentUser != null) {
			newPost.setUsersUnfollowedThisPostByAuthor(postByCurrentUser.getUsersUnfollowedThisPostByAuthor());
		}
		newPost = messagingRepository.save(newPost);
		return convertPostToPostDto(newPost);
	}
	
	@Override
	public PostDto removePost(String currentUserId, String postId) throws Throwable {
		try {
			Post post = messagingRepository.findById(postId).orElseThrow(() -> new PostNotFoundException());
			if (currentUserId.equalsIgnoreCase(post.getAuthorId())) {
				messagingRepository.delete(post);
				return convertPostToPostDto(post);
			} else
				throw new AccessException("Access denied: you'r not author!");
		} catch (Exception e) {
			throw new PostNotFoundException();
		}
	}

	private PostDto convertPostToPostDto(Post post) {
		return PostDto.builder()
				.id(post.getId())
				.authorId(post.getAuthorId())
				.dateOfPublish(post.getDateOfPublish())
				.pictures(post.getPictures())
				.text(post.getText())
				.build();
	}

	@Override
	public PostDto editPost(String currentUserId, PostEditDto postEditDto, String postId) throws Throwable {
		try {
			Post post = messagingRepository.findById(postId).orElseThrow(() -> new PostNotFoundException());
			if (currentUserId.equalsIgnoreCase(post.getAuthorId())) {
				if (postEditDto.getText() != null) {
					post.setText(postEditDto.getText());
				}
				if (postEditDto.getPictures().size() != 0) {
					post.setPictures(postEditDto.getPictures());
				}
				post.setDateOfPublish(LocalDateTime.now());
				messagingRepository.save(post);
				return convertPostToPostDto(post);
			} else
				throw new AccessException("Access denied: you'r not author!");
		} catch (Exception e) {
			throw new DataFormatException();
		}
	}
	
	@Override
	public void makePostFavorite(String currentUserId, String postId) throws Throwable {
		try {
			Post post = messagingRepository.findById(postId).orElseThrow(() -> new PostNotFoundException());
			if (post.getUsersAddedThisPostToFavorites().contains(currentUserId)) {
				post.removeUserThatAddedThisPostToFav(currentUserId);
				messagingRepository.save(post);
			} else {
				post.addUserThatAddedThisPostToFav(currentUserId);
				messagingRepository.save(post);
			}
		} catch (PostNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void makePostHidden(String currentUserId, String postId) throws Throwable {
		try {
			Post post = messagingRepository.findById(postId).orElseThrow(() -> new PostNotFoundException());
			if (!currentUserId.equalsIgnoreCase(post.getAuthorId())) {
				post.addUserThatHidThisPost(currentUserId);
				messagingRepository.save(post);
			} else
				throw new AccessException("Access denied: you'r not author!");
		} catch (PostNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void unfollowPostsByUser(String currentUserId, String postId) throws Throwable {
		try {
			Post post = messagingRepository.findById(postId).orElseThrow(() -> new PostNotFoundException());
			if (!currentUserId.equalsIgnoreCase(post.getAuthorId())) {
				String userIdToUnfollow = post.getAuthorId();
				messagingRepository.findByAuthorId(userIdToUnfollow)
						.filter(item -> item.addUserThatUnfollowedThisPostByAuthor(currentUserId))
						.forEach(i -> messagingRepository.save(i));
			} else
				throw new AccessException("Access denied: you'r not author!");
		} catch (PostNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Iterable<PostDto> getAllFavoritePostsByUser(String userId) { //проверить, есть ли такой в базе аккаунтинга, или 0 в результате хватит?
		Iterable<PostDto> favoritesByUser = messagingRepository.findAll()
				.stream()
				.filter(post -> post.getUsersAddedThisPostToFavorites().contains(userId))
				.map(post -> convertPostToPostDto(post))
				.collect(Collectors.toList());
		return favoritesByUser;
	}

	@Override
	public void cleanPostsAndPresenceOfRemovedUser(String removedUserId) {
		messagingRepository.findByAuthorId(removedUserId)
						.forEach(i -> messagingRepository.delete(i));
		
		messagingRepository.findAll()
						.stream()
						.filter(post -> post.getUsersAddedThisPostToFavorites().contains(removedUserId))
						.forEach(i -> i.getUsersAddedThisPostToFavorites().remove(removedUserId));
		
		messagingRepository.findAll()
						.stream()
						.filter(post -> post.getUsersHidThisPost().contains(removedUserId))
						.forEach(i -> i.getUsersHidThisPost().remove(removedUserId));
		
		messagingRepository.findAll()
						.stream()
						.filter(post -> post.getUsersUnfollowedThisPostByAuthor().contains(removedUserId))
						.forEach(i -> i.getUsersUnfollowedThisPostByAuthor().remove(removedUserId));
	}

	@Override
	public Iterable<PostDto> getUserPostFeed(String currentUserId) {
		Iterable<PostDto> list = messagingRepository.findAll()
								.stream()
								.filter(post->(!post.getUsersHidThisPost().contains(currentUserId))&&(!post.getUsersUnfollowedThisPostByAuthor().contains(currentUserId)))
								.sorted((p1,p2)->p1.getDateOfPublish().compareTo(p2.getDateOfPublish()))
								.map(post -> convertPostToPostDto(post))
								.collect(Collectors.toList());
		return list;
	}
}
