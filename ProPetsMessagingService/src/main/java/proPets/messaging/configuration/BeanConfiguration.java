package proPets.messaging.configuration;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
@Getter
public class BeanConfiguration {
	private int quantity;
	private String baseJWTUrl;

	public BeanConfiguration(int quantity, String baseJWTUrl) {
		this.quantity = quantity;
		this.baseJWTUrl = baseJWTUrl;
	}
}
