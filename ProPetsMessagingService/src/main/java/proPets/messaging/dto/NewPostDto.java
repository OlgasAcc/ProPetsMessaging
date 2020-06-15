package proPets.messaging.dto;

import java.util.Set;

import lombok.Getter;

@Getter
public class NewPostDto {
	
	String authorAvatar;
	String authorName;
	String text;
	Set<String> pictures;
}
