package image.exifweb.web.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 11/22/13
 * Time: 9:49 PM
 * To change this template use File | Settings | File Templates.
 */
@WebSecurityComponent
public class AuthSuccessHandler implements AuthenticationSuccessHandler {
	private static final Logger logger = LoggerFactory.getLogger(AuthSuccessHandler.class);
	@Autowired
	private ObjectMapper objectMapper;
	private AuthUtil authUtil = new AuthUtil();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
	                                    Authentication authentication) throws IOException {
		logger.debug("Log on user: {}",
				SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		response.setContentType("application/json");
		AuthData authData = this.authUtil.getAuthData();
		this.objectMapper.writeValue(response.getOutputStream(), new AuthCheckResponse(authData));
	}
}
