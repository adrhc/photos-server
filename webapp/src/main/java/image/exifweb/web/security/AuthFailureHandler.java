package image.exifweb.web.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 11/23/13
 * Time: 1:51 AM
 * To change this template use File | Settings | File Templates.
 */
@WebSecurityComponent
public class AuthFailureHandler implements AuthenticationFailureHandler {
	@Inject
	private ObjectMapper objectMapper;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException {
		response.setContentType("application/json");
		this.objectMapper.writeValue(response.getOutputStream(), AuthCheckResponse.FAILED_AUTHENTICATION);
	}
}
