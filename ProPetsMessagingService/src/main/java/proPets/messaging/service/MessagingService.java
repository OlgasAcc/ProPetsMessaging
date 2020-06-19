package proPets.messaging.service;

import org.springframework.web.servlet.ModelAndView;

import proPets.messaging.dto.NewPostDto;
import proPets.messaging.dto.PostEditDto;

public interface MessagingService {

	ModelAndView addPost(String currentUserId, NewPostDto newPostDto) throws Exception;

	ModelAndView removePost(String currentUserId, String postId) throws Throwable;

	ModelAndView editPost(String currentUserId, PostEditDto postEditDto, String postId) throws Throwable;

	void makePostFavorite(String currentUserId, String postId) throws Throwable;

	ModelAndView makePostHidden(String currentUserId, String postId, int page) throws Throwable;

	ModelAndView unfollowPostsByUser(String currentUserId, String postId) throws Throwable;

	ModelAndView getAllFavoritePostsByUser(String userId, int page);

	ModelAndView getUserPostFeed(String currentUserId, int page);
	
	void cleanPostsAndPresenceOfRemovedUser(String removedUserId);

}
