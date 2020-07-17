package proPets.messaging.service;

import java.util.List;

import proPets.messaging.dto.NewPostDto;
import proPets.messaging.dto.PostDto;
import proPets.messaging.dto.PostEditDto;
import proPets.messaging.dto.UserRemoveDto;

public interface MessagingService {
	
	List<PostDto> addPost(String currentUserId, NewPostDto newPostDto) throws Exception;

	List<PostDto> removePost(String currentUserId, String postId) throws Throwable;

	List<PostDto> editPost(String currentUserId, PostEditDto postEditDto, String postId) throws Throwable;

	void makePostFavorite(String currentUserId, String postId) throws Throwable;

	List<PostDto> makePostHidden(String currentUserId, String postId, int page) throws Throwable;

	List<PostDto> unfollowPostsByUser(String currentUserId, String postId) throws Throwable;

	List<PostDto> getAllFavoritePostsByUser(String currentUserId, int page);

	List<PostDto> getUserPostFeed(String currentUserId, int page);
	
	String cleanPostsAndPresenceOfRemovedUser(UserRemoveDto removedUserId);

}

//ModelAndView addPost(String currentUserId, NewPostDto newPostDto) throws Exception;
