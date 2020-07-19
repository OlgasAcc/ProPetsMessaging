package proPets.messaging.controller;

import java.security.Principal;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestHeader;
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

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/message/v1")
public class MessagingServiceController {

	@Autowired
	MessagingService messagingService;
	
	@Autowired
	MessagingConfiguration messagingConfiguration;

	@RefreshScope
	@GetMapping("/config")
	public  BeanConfiguration getRefreshedData() {
		return new BeanConfiguration(messagingConfiguration.getQuantity());
	}
	
	@PostMapping("/post")
	public List<PostDto> addPost(@RequestHeader(value = "Authorization") String authorization,
			Principal principal, @RequestBody NewPostDto newPostDto) throws Exception {
		return messagingService.addPost(principal.getName(), newPostDto);
	}

	@DeleteMapping("/post/{postId}")
	public List<PostDto> removePost(@RequestHeader(value = "Authorization") String authorization,
			Principal principal, @PathVariable String postId) throws Throwable {
		return messagingService.removePost(principal.getName(), postId);
	}

	@PutMapping("/post/{postId}")
	public List<PostDto> editPost(@RequestHeader(value = "Authorization") String authorization,
			Principal principal, @RequestBody PostEditDto postEditDto, @PathVariable String postId) throws Throwable {
		return messagingService.editPost(principal.getName(), postEditDto, postId);
	}

	@PostMapping("/post/favorites/{postId}")
	public void makePostFavorite(@RequestHeader(value = "Authorization") String authorization,
			@PathVariable String postId, Principal principal) throws Throwable {
		messagingService.makePostFavorite(principal.getName(), postId);
	}

	@PostMapping("/post/hidden/{postId}")
	public List<PostDto> makePostHidden(@RequestHeader(value = "Authorization") String authorization,
			@PathVariable String postId, @RequestParam ("page") int page, Principal principal) throws Throwable {
		return messagingService.makePostHidden(principal.getName(), postId, page);
	}

	@PostMapping("/post/unfollow/{postId}")
	public List<PostDto> unfollowPostsByUser(@RequestHeader(value = "Authorization") String authorization,
			@PathVariable String postId, Principal principal) throws Throwable {
		return messagingService.unfollowPostsByUser(principal.getName(), postId);
	}

	@GetMapping("/post/favorites")
	public List<PostDto> getAllFavoritePostsByUser(@RequestHeader(value = "Authorization") String authorization,
			Principal principal, @RequestParam ("page") int page) {
		return messagingService.getAllFavoritePostsByUser(principal.getName(), page);
	}

	@GetMapping("/post/feed")
	public List<PostDto> getUserPostFeed(@RequestHeader(value = "Authorization") String authorization, @RequestParam ("page") int page,
			Principal principal) {
		return messagingService.getUserPostFeed(principal.getName(), page);
	}
	
	// for front: this request is working with "remove user" in Accounting service:
	// it is cleaning the "tail of removed user" AFTER removing the user from
	// account db
	
	@DeleteMapping("/post/cleaner")
	public ResponseEntity<String> cleanPostsAndPresenceOfRemovedUser(@RequestBody UserRemoveDto userRemoveDto) {
		return ResponseEntity.ok(messagingService.cleanPostsAndPresenceOfRemovedUser(userRemoveDto));
	}

}
