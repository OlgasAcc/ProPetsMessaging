package proPets.messaging.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import proPets.messaging.configuration.BeanConfiguration;
import proPets.messaging.configuration.MessagingConfiguration;
import proPets.messaging.dto.NewPostDto;
import proPets.messaging.dto.PostDto;
import proPets.messaging.dto.PostEditDto;
import proPets.messaging.dto.UserRemoveDto;
import proPets.messaging.service.MessagingService;
testtttttttttttttt!git S
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/message/v1")

public class MessagingServiceController {

	@Autowired
	MessagingService messagingService;

	@Autowired
	MessagingConfiguration messagingConfiguration;

	@Autowired
	private HttpServletRequest requestContext;

	@RefreshScope
	@GetMapping("/config")
	public BeanConfiguration getRefreshedData() {
		return new BeanConfiguration(messagingConfiguration.getQuantity());
	}

	@PostMapping("/post")
	public List<PostDto> addPost(@RequestBody NewPostDto newPostDto) throws Exception {
		String authorId = requestContext.getHeader("authorId");
		System.out.println(authorId);
		return messagingService.addPost(authorId, newPostDto);
	}

	@DeleteMapping("/post/{postId}")
	public List<PostDto> removePost(@PathVariable String postId) throws Throwable {
		String authorId = requestContext.getHeader("authorId");
		return messagingService.removePost(authorId, postId);
	}

	@PutMapping("/post/{postId}")
	public List<PostDto> editPost(@RequestBody PostEditDto postEditDto, @PathVariable String postId) throws Throwable {
		String authorId = requestContext.getHeader("authorId");
		return messagingService.editPost(authorId, postEditDto, postId);
	}

	@PostMapping("/post/favorites/{postId}")
	public void makePostFavorite(@PathVariable String postId) throws Throwable {
		String authorId = requestContext.getHeader("authorId");
		messagingService.makePostFavorite(authorId, postId);
	}

	@PostMapping("/post/hidden/{postId}")
	public List<PostDto> makePostHidden(@PathVariable String postId, @RequestParam("page") int page) throws Throwable {
		String authorId = requestContext.getHeader("authorId");
		return messagingService.makePostHidden(authorId, postId, page);
	}

	@PostMapping("/post/unfollow/{postId}")
	public List<PostDto> unfollowPostsByUser(@PathVariable String postId) throws Throwable {
		String authorId = requestContext.getHeader("authorId");
		return messagingService.unfollowPostsByUser(authorId, postId);
	}

	@GetMapping("/post/favorites")
	public List<PostDto> getAllFavoritePostsByUser(@RequestParam("page") int page) {
		String authorId = requestContext.getHeader("authorId");
		return messagingService.getAllFavoritePostsByUser(authorId, page);
	}

	@GetMapping("/post/feed")
	public List<PostDto> getUserPostFeed(@RequestParam("page") int page) {
		String authorId = requestContext.getHeader("authorId");
		return messagingService.getUserPostFeed(authorId, page);
	}

	// for front: this request is working with "remove user" in Accounting service:
	// it is cleaning the "tail of removed user" AFTER removing the user from
	// account db

	@DeleteMapping("/post/cleaner")
	public ResponseEntity<String> cleanPostsAndPresenceOfRemovedUser(@RequestBody UserRemoveDto userRemoveDto) {
		return ResponseEntity.ok(messagingService.cleanPostsAndPresenceOfRemovedUser(userRemoveDto));
	}

}
