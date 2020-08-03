package proPets.messaging.service.utils;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import proPets.messaging.configuration.MessagingConfiguration;
import proPets.messaging.dao.MessagingRepository;
import proPets.messaging.dto.PostDto;
import proPets.messaging.model.Post;

@Component

public class MessageUtil implements Serializable {

	@Autowired
	MessagingRepository messagingRepository;
	
	@Autowired
	MessagingConfiguration messagingConfiguration;

	private static final long serialVersionUID = -2550185165626007488L;
	
	public PostDto convertPostToPostDto(Post post) {
		return PostDto.builder()
				.id(post.getId())
				.authorData(post.getAuthorData())
				.dateOfPublish(post.getDateOfPublish())
				.pictures(post.getPictures())
				.text(post.getText())
				.build();
	}
	
	public List<PostDto> getListAndConvertToListOfPostDto (String currentUserId, PageRequest pageReq){
		Page<Post> posts = messagingRepository.findAll(pageReq);
		return posts.getContent().stream()
				.filter(post->(!post.getUsersHidThisPost().contains(currentUserId))&&(!post.getUsersUnfollowedThisPostByAuthor().contains(currentUserId)))
				//.sorted((p1,p2)->p2.getDateOfPublish().compareTo(p1.getDateOfPublish()))
				.map(this::convertPostToPostDto)
				.collect(Collectors.toList());
	}
	
	public List<PostDto> getFavsListAndConvertToListOfPostDto (String currentUserId, PageRequest pageReq){
		Query query = new Query();
		query.addCriteria(Criteria.where(currentUserId).in("usersAddedThisPostToFavorites"));
		Page<Post> posts = messagingRepository.findByUsersAddedThisPostToFavoritesAuthorId(currentUserId,pageReq);
		return posts.getContent().stream()
				.map(this::convertPostToPostDto)
				.collect(Collectors.toList());
	}
	
	
	
	
//	public PagedListHolder<PostDto> createPageListHolder(String currentUserId, int pageNumber, int quantity) {	
//		List<PostDto> list = getUpdatedFilteredPostFeed(currentUserId);
//		PagedListHolder<PostDto> pagedListHolder = new PagedListHolder<>(list);
//		pagedListHolder.setPage(pageNumber);
//		pagedListHolder.setPageSize(quantity);
//		return pagedListHolder;
//	}
//	
//	public ModelAndView createModelAndViewObject (PagedListHolder<PostDto> pagedListHolder, int page, int pageSize) {
//		ModelAndView mav = new ModelAndView("list of posts", HttpStatus.OK);
//		mav.addObject("pagedList", pagedListHolder.getPageList());
//		mav.addObject("page", 0);
//		mav.addObject("maxPage", pagedListHolder.getPageCount());
//		return mav;
//	}
//	
//	public List<PostDto> getUpdatedFilteredPostFeed(String currentUserId){
//		List<PostDto> list = messagingRepository.findAll()
//				.stream()
//				.filter(post->(!post.getUsersHidThisPost().contains(currentUserId))&&(!post.getUsersUnfollowedThisPostByAuthor().contains(currentUserId)))
//				.sorted((p1,p2)->p2.getDateOfPublish().compareTo(p1.getDateOfPublish()))
//				 .map(this::convertPostToPostDto)
//				.collect(Collectors.toList());
//		return list;
//	}

}
