package proPets.messaging.configuration;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
@Getter
public class BeanConfiguration {
	private int quantity;

	public BeanConfiguration(int quantity) {
		this.quantity = quantity;
	}
}
