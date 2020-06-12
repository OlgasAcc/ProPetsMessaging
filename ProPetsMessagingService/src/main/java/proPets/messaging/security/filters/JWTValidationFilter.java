package proPets.messaging.security.filters;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import proPets.messaging.configuration.MessagingConfiguration;
import proPets.messaging.dao.MessagingRepository;
import proPets.messaging.dto.AuthResponse;

@Service
@Order(10)

public class JWTValidationFilter implements Filter {

	@Autowired
	MessagingRepository accountRepository;

	@Autowired
	MessagingConfiguration accountConfiguration;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String path = request.getServletPath();
		String auth = request.getHeader("Authorization");

		if (path.startsWith("/message/v1/post")) {
			if (auth.startsWith("Bearer")) {
				String newToken;
				String email;
				try {
					ResponseEntity<AuthResponse> newResponse = getHeadersWithNewToken(auth);

					newToken = newResponse.getHeaders().getFirst("X-token");
					email = newResponse.getBody().getEmail();

					response.setHeader("X-token", newToken);
					chain.doFilter(new WrapperRequest(request, email), response);
					return;
				} catch (Exception e) {
					response.sendError(401, "Header Authorization is not valid");
					return;
				}
			} else {
				response.sendError(401, "Token format is wrong");
				return;
			}
		}
		System.out.println("validation filter did not work");
		chain.doFilter(request, response);
	}

	private class WrapperRequest extends HttpServletRequestWrapper {
		String email;

		public WrapperRequest(HttpServletRequest request, String email) {
			super(request);
			this.email = email;
		}

		@Override
		public Principal getUserPrincipal() {
			return new Principal() {

				@Override
				public String getName() {
					return email;
				}
			};
		}
	}

	private ResponseEntity<AuthResponse> getHeadersWithNewToken(String auth) {
		RestTemplate restTemplate = new RestTemplate();

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Authorization", auth);
		headers.add("Content-Type", "application/json");

		String url = "https://propets-accounting-service.herokuapp.com/security/v1/verify";
		//String url = "http://localhost:8080/security/v1/verify";
		try {
			RequestEntity<Object> request = new RequestEntity<>(headers, HttpMethod.POST, URI.create(url));
			ResponseEntity<AuthResponse> newResponse = restTemplate.exchange(request, AuthResponse.class);
			if (newResponse.getStatusCode().is2xxSuccessful()) {
				return newResponse;
			} else {
				throw new RuntimeException("Validation is failed");
			}
		} catch (HttpClientErrorException e) {
			throw new RuntimeException("Validation is failed");
		}
	}
}
