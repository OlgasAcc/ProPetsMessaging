package proPets.messaging.service;

import proPets.messaging.dto.NewPostDto;
import proPets.messaging.dto.PostDto;
import proPets.messaging.dto.PostEditDto;

public interface MessagingService {

	PostDto addPost(String currentUserId, NewPostDto newPostDto);

	PostDto removePost(String currentUserId, String postId) throws Throwable;

	PostDto editPost(String currentUserId, PostEditDto postEditDto, String postId) throws Throwable;

	void makePostFavorite(String currentUserId, String postId) throws Throwable;

	void makePostHidden(String currentUserId, String postId) throws Throwable;

	void unfollowPostsByUser(String currentUserId, String postId) throws Throwable;

	Iterable<PostDto> getAllFavoritePostsByUser(String userId);

	void cleanPostsAndPresenceOfRemovedUser(String removedUserId);

	Iterable<PostDto> getUserPostFeed(String currentUserId);

}
