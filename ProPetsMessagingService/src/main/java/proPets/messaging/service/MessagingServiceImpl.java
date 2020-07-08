package proPets.messaging.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.expression.AccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import proPets.messaging.configuration.MessagingConfiguration;
import proPets.messaging.dao.MessagingRepository;
import proPets.messaging.dto.NewPostDto;
import proPets.messaging.dto.PostDto;
import proPets.messaging.dto.PostEditDto;
import proPets.messaging.dto.UserRemoveDto;
import proPets.messaging.exceptions.PostNotFoundException;
import proPets.messaging.model.Post;
import proPets.messaging.service.utils.MessageUtil;

@Service
public class MessagingServiceImpl implements MessagingService {

	@Autowired
	MessagingRepository messagingRepository;

	@Autowired
	MessagingConfiguration messagingConfiguration;
	
	@Autowired
	MessageUtil messageUtil;

	@Override
	public ModelAndView addPost(String currentUserId, NewPostDto newPostDto) throws Exception{
		Post newPost = new Post(newPostDto.getText(), currentUserId, newPostDto.getAuthorAvatar(), newPostDto.getAuthorName(), newPostDto.getPictures());
		Post postByCurrentUser = messagingRepository.findByAuthorId(currentUserId).findFirst().orElse(null);
		if (postByCurrentUser != null) {
			newPost.setUsersUnfollowedThisPostByAuthor(postByCurrentUser.getUsersUnfollowedThisPostByAuthor());
		}
		messagingRepository.save(newPost);
		
		int quantity = messagingConfiguration.getQuantity();
		PagedListHolder<PostDto> pagedListHolder = messageUtil.createPageListHolder(currentUserId, 0, quantity);
		return messageUtil.createModelAndViewObject(pagedListHolder, 0, quantity);
	} 
	
	@Override
	public ModelAndView removePost(String currentUserId, String postId) throws Throwable {
		try {
			Post post = messagingRepository.findById(postId).orElseThrow(() -> new PostNotFoundException());
			if (currentUserId.equalsIgnoreCase(post.getAuthorId())) {
				messagingRepository.delete(post);
				int quantity = messagingConfiguration.getQuantity();
				PagedListHolder<PostDto> pagedListHolder = messageUtil.createPageListHolder(currentUserId, 0, quantity);
				return messageUtil.createModelAndViewObject(pagedListHolder, 0, quantity);
			} else
				throw new AccessException("Access denied: you'r not author!");
		} catch (Exception e) {
			throw new PostNotFoundException();
		}
	}


	@Override
	public ModelAndView editPost(String currentUserId, PostEditDto postEditDto, String postId) throws Throwable {
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
				
				int quantity = messagingConfiguration.getQuantity();
				PagedListHolder<PostDto> pagedListHolder = messageUtil.createPageListHolder(currentUserId, 0, quantity);
				return messageUtil.createModelAndViewObject(pagedListHolder, 0, quantity);
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
		} catch (Exception e) {
			throw new Exception();
		}
	}
	
	//user stays on the same page number
	@Override
	public ModelAndView makePostHidden(String currentUserId, String postId, int page) throws Throwable {
		try {
			Post post = messagingRepository.findById(postId).orElseThrow(() -> new PostNotFoundException());
			if (!currentUserId.equalsIgnoreCase(post.getAuthorId())) {
				post.addUserThatHidThisPost(currentUserId);
				messagingRepository.save(post);
				int quantity = messagingConfiguration.getQuantity();
				PagedListHolder<PostDto> pagedListHolder = messageUtil.createPageListHolder(currentUserId, page, quantity);
				return messageUtil.createModelAndViewObject(pagedListHolder, page, quantity);
			} else
				throw new AccessException("Access denied: you'r not author!");
		} catch (Exception e) {
			throw new Exception();
		}
	}

	//user goes to the first page of the feed
	@Override
	public ModelAndView unfollowPostsByUser(String currentUserId, String postId) throws Throwable {
		try {
			Post post = messagingRepository.findById(postId).orElseThrow(() -> new PostNotFoundException());
			if (!currentUserId.equalsIgnoreCase(post.getAuthorId())) {
				String userIdToUnfollow = post.getAuthorId();
				messagingRepository.findByAuthorId(userIdToUnfollow)
						.filter(item -> item.addUserThatUnfollowedThisPostByAuthor(currentUserId))
						.forEach(i -> messagingRepository.save(i));
				
				int quantity = messagingConfiguration.getQuantity();
				PagedListHolder<PostDto> pagedListHolder = messageUtil.createPageListHolder(currentUserId, 0, quantity);
				return messageUtil.createModelAndViewObject(pagedListHolder, 0, quantity);
			} else
				throw new AccessException("Access denied: you'r not author!");
		} catch (Exception e) {
			throw new Exception();
		}
	}
	
   //need to save current page number in Store (front) for updating page or repeat the last request
	@Override
	public ModelAndView getAllFavoritePostsByUser(String userId, int page) { //проверить, есть ли такой в базе аккаунтинга, или 0 в результате хватит?
		List<PostDto> favoritesByUser = messagingRepository.findAll()
				.stream()
				.filter(post -> post.getUsersAddedThisPostToFavorites().contains(userId))
				.map(post -> messageUtil.convertPostToPostDto(post))
				.collect(Collectors.toList());
		
		int quantity = messagingConfiguration.getQuantity();
		PagedListHolder<PostDto> pagedListHolder = new PagedListHolder<PostDto>(favoritesByUser);
		pagedListHolder.setPage(page);
		pagedListHolder.setPageSize(quantity);
		return messageUtil.createModelAndViewObject(pagedListHolder, page, quantity);
	}

	@Override
	public String cleanPostsAndPresenceOfRemovedUser(UserRemoveDto userRemoveDto) {
		String authorId = userRemoveDto.getUserId();
		messagingRepository.findByAuthorId(authorId)
						.forEach(i -> messagingRepository.delete(i));
		
		messagingRepository.findAll()
						.stream()
						.filter(post -> post.getUsersAddedThisPostToFavorites().contains(authorId))
						.forEach(i -> i.getUsersAddedThisPostToFavorites().remove(authorId));
		
		messagingRepository.findAll()
						.stream()
						.filter(post -> post.getUsersHidThisPost().contains(authorId))
						.forEach(i -> i.getUsersHidThisPost().remove(authorId));
		
		messagingRepository.findAll()
						.stream()
						.filter(post -> post.getUsersUnfollowedThisPostByAuthor().contains(authorId))
						.forEach(i -> i.getUsersUnfollowedThisPostByAuthor().remove(authorId));
		return authorId;
	}
	
	//need to save current page number in Store (front) for updating page or repeat the last request
	@Override
	public ModelAndView getUserPostFeed(String currentUserId, int page) {		
		int quantity = messagingConfiguration.getQuantity();
		PagedListHolder<PostDto> pagedListHolder = messageUtil.createPageListHolder(currentUserId, page, quantity);
		return messageUtil.createModelAndViewObject(pagedListHolder, page, quantity);
	}
}
