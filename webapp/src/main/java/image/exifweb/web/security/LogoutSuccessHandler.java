package image.exifweb.web.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
 * Date: 11/27/13
 * Time: 1:02 AM
 * To change this template use File | Settings | File Templates.
 */
@WebSecurityComponent
public class LogoutSuccessHandler implements
		org.springframework.security.web.authentication.logout.LogoutSuccessHandler {
	private static final Logger logger = LoggerFactory.getLogger(AuthSuccessHandler.class);
	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
	                            Authentication authentication) throws IOException, ServletException {
		logger.debug("Log off user: {}", authentication);
		Map<String, Object> success = new HashMap<String, Object>();
		success.put("success", "true");
		success.put("error", "false");
		response.setContentType("application/json");
		objectMapper.writeValue(response.getOutputStream(), success);
	}
}
