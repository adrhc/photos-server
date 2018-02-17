package image.exifweb.util.frameworks.spring.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 11/23/13
 * Time: 1:51 AM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class AuthFailureHandler implements AuthenticationFailureHandler {
	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
	                                    AuthenticationException exception) throws IOException, ServletException {
		Map<String, String> success = new HashMap<String, String>();
		success.put("success", "false");
		success.put("error", "true");
		response.setContentType("application/json");
		objectMapper.writeValue(response.getOutputStream(), success);
	}
}
