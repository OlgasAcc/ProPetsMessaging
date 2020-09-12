package proPets.messaging.service;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.zip.DataFormatException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.expression.AccessException;
import org.springframework.stereotype.Service;

import proPets.messaging.configuration.MessagingConfiguration;
import proPets.messaging.dao.MessagingRepository;
import proPets.messaging.dto.NewPostDto;
import proPets.messaging.dto.PostDto;
import proPets.messaging.dto.PostEditDto;
import proPets.messaging.dto.UserRemoveDto;
import proPets.messaging.exceptions.PostNotFoundException;
import proPets.messaging.model.AuthorData;
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
	public List<PostDto> addPost(String currentUserId, NewPostDto newPostDto) throws Exception {
		AuthorData authorData = AuthorData.builder()
				.authorId(currentUserId)
				.authorName(newPostDto.getAuthorName())
				.authorAvatar(newPostDto.getAuthorAvatar())
				.build();
		Post newPost = new Post(newPostDto.getText(), newPostDto.getPictures());
		newPost.setAuthorData(authorData);
		Post anyPostByCurrentUser = messagingRepository.findByAuthorDataAuthorId(currentUserId).findFirst().orElse(null);
		if (anyPostByCurrentUser != null) {
			newPost.setUsersUnfollowedThisPostByAuthor(anyPostByCurrentUser.getUsersUnfollowedThisPostByAuthor());
		}
		messagingRepository.save(newPost);
		int quantity = messagingConfiguration.getQuantity();
		Pageable pageReq = PageRequest.of(0, quantity, Sort.Direction.DESC, "dateOfPublish");
		return messageUtil.getListAndConvertToListOfPostDto(currentUserId, pageReq);
	}

	@Override
	public List<PostDto> removePost(String currentUserId, String postId) throws Throwable {
		try {
			Post post = messagingRepository.findById(postId).orElseThrow(() -> new PostNotFoundException());
			if (currentUserId.equalsIgnoreCase(post.getAuthorData().getAuthorId())) {
				messagingRepository.delete(post);
				int quantity = messagingConfiguration.getQuantity();
				Pageable pageReq = PageRequest.of(0, quantity, Sort.Direction.DESC, "dateOfPublish");
				return messageUtil.getListAndConvertToListOfPostDto(currentUserId, pageReq);
			} else
				throw new AccessException("Access denied: you'r not author!");
		} catch (Exception e) {
			throw new PostNotFoundException();
		}
	}

	@Override
	public List<PostDto> editPost(String currentUserId, PostEditDto postEditDto, String postId) throws Throwable {
		try {
			Post post = messagingRepository.findById(postId).orElseThrow(() -> new PostNotFoundException());
			if (currentUserId.equalsIgnoreCase(post.getAuthorData().getAuthorId())) {
				if (postEditDto.getText() != null) {
					post.setText(postEditDto.getText());
				}
				if (postEditDto.getPictures().size() != 0) {
					post.setPictures(postEditDto.getPictures());
				}
				post.setDateOfPublish(LocalDateTime.now());
				messagingRepository.save(post);

				int quantity = messagingConfiguration.getQuantity();
				Pageable pageReq = PageRequest.of(0, quantity, Sort.by(Order.desc("dateOfPublish")));
				return messageUtil.getListAndConvertToListOfPostDto(currentUserId, pageReq);
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
	
			if (post.removeFromUserThatAddedThisPostToFav(currentUserId)) {
				messagingRepository.save(post);
			} else {
				post.addToUserThatAddedThisPostToFav(currentUserId);
				messagingRepository.save(post);
			}
		} catch (Exception e) {
			throw new Exception();
		}
	}

	// user stays on the same page number
	@Override
	public List<PostDto> makePostHidden(String currentUserId, String postId, int page) throws Throwable {
			Post post = messagingRepository.findById(postId).orElseThrow(() -> new PostNotFoundException());
			if (!currentUserId.equalsIgnoreCase(post.getAuthorData().getAuthorId())) {
				post.addToUsersThatHidThisPost(currentUserId);
				messagingRepository.save(post);
				int quantity = messagingConfiguration.getQuantity();
				Pageable pageReq = PageRequest.of(page, quantity, Sort.Direction.DESC, "dateOfPublish");
				return messageUtil.getListAndConvertToListOfPostDto(currentUserId, pageReq);
			} else
				throw new AccessException("This is your post");
	}

	// user goes to the first page of the feed
	@Override
	public List<PostDto> unfollowPostsByUser(String currentUserId, String postId) throws Throwable {
			Post post = messagingRepository.findById(postId).orElseThrow(() -> new PostNotFoundException());
			if (!currentUserId.equalsIgnoreCase(post.getAuthorData().getAuthorId())) {
				String userIdToUnfollow = post.getAuthorData().getAuthorId();
				messagingRepository.findByAuthorDataAuthorId(userIdToUnfollow)
						.filter(item -> item.addToUsersThatUnfollowedThisPostByAuthor(currentUserId))
						.forEach(i -> messagingRepository.save(i));

				int quantity = messagingConfiguration.getQuantity();
				Pageable pageReq = PageRequest.of(0, quantity, Sort.Direction.DESC, "dateOfPublish");
				return messageUtil.getListAndConvertToListOfPostDto(currentUserId, pageReq);
			} else
				throw new AccessException("Access denied: you'r author!");
	}

	// need to save current page number in Store (front) for updating page or repeat
	// the last request
	@Override
	public List<PostDto> getAllFavoritePostsByUser(String currentUserId, int page) { // проверить, есть ли такой в базе
																				// аккаунтинга, или 0 в результате
																			// хватит?
		int quantity = messagingConfiguration.getQuantity();
		Pageable pageReq = PageRequest.of(page, quantity, Sort.Direction.DESC, "dateOfPublish");
		return messageUtil.getFavsListAndConvertToListOfPostDto(currentUserId, pageReq);
	}
	

	@Override
	public String cleanPostsAndPresenceOfRemovedUser(UserRemoveDto userRemoveDto) {
		String authorId = userRemoveDto.getUserId();
		messagingRepository.findByAuthorDataAuthorId(authorId).forEach(i -> messagingRepository.delete(i));

		messagingRepository.findByUsersAddedThisPostToFavoritesAuthorId(authorId).stream()
				.forEach(p->p.removeFromUserThatAddedThisPostToFav(authorId));			

		messagingRepository.findAll().stream().filter(post -> post.getUsersHidThisPost().contains(authorId))
				.forEach(i -> i.getUsersHidThisPost().remove(authorId));

		messagingRepository.findAll().stream()
				.filter(post -> post.getUsersUnfollowedThisPostByAuthor().contains(authorId))
				.forEach(i -> i.getUsersUnfollowedThisPostByAuthor().remove(authorId));
		return authorId;
	}

	// need to save current page number in Store (front) for updating page or repeat
	// the last request
	@Override
	public List<PostDto> getUserPostFeed(String currentUserId, int page) {
		int quantity = messagingConfiguration.getQuantity();
		Pageable pageReq = PageRequest.of(page, quantity, Sort.Direction.DESC, "dateOfPublish");
		return messageUtil.getListAndConvertToListOfPostDto(currentUserId, pageReq);
	}
}



//@Override
//public ModelAndView addPost(String currentUserId, NewPostDto newPostDto) throws Exception{
//	Post newPost = new Post(newPostDto.getText(), currentUserId, newPostDto.getAuthorAvatar(), newPostDto.getAuthorName(), newPostDto.getPictures());
//	Post postByCurrentUser = messagingRepository.findByAuthorId(currentUserId).findFirst().orElse(null);
//	if (postByCurrentUser != null) {
//		newPost.setUsersUnfollowedThisPostByAuthor(postByCurrentUser.getUsersUnfollowedThisPostByAuthor());
//	}
//	messagingRepository.save(newPost);
//	
//	int quantity = messagingConfiguration.getQuantity();
//	PagedListHolder<PostDto> pagedListHolder = messageUtil.createPageListHolder(currentUserId, 0, quantity);
//	return messageUtil.createModelAndViewObject(pagedListHolder, 0, quantity);
//} 
