package proPets.messaging.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import proPets.messaging.model.AuthorData;

@AllArgsConstructor
@Getter
@Setter
@Builder

public class PostDto {

	String id;
	String text;
	Set<String> pictures;
	LocalDateTime dateOfPublish;
	AuthorData authorData;
}
