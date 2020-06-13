package proPets.messaging.security.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.GenericFilterBean;

@Service
@Order(10)

public class CORSFilter  extends GenericFilterBean {

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "Authorization, X-Token, X-id, Content-Type");
		response.addHeader("Access-Control-Expose-Headers", "xsrf-token, X-Token, X-id");
		chain.doFilter(req, res);
	}
}
