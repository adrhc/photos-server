package image.exifweb.web.security;

import image.exifweb.util.frameworks.hibernate.HibernateAwareObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

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
@Service
public class LogoutSuccessHandler implements
		org.springframework.security.web.authentication.logout.LogoutSuccessHandler {
	private static final Logger logger = LoggerFactory.getLogger(AuthSuccessHandler.class);
	@Autowired
	private HibernateAwareObjectMapper hibernateAwareObjectMapper;

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
	                            Authentication authentication) throws IOException, ServletException {
		logger.debug("Log off user: {}", authentication);
		Map<String, Object> success = new HashMap<String, Object>();
		success.put("success", "true");
		success.put("error", "false");
		response.setContentType("application/json");
		hibernateAwareObjectMapper.writeValue(response.getOutputStream(), success);
	}
}
