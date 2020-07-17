package proPets.messaging.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

public class AuthorData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String authorId;
	String authorAvatar;
	String authorName;
	
}
