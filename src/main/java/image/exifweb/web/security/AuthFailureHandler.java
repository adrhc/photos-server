package image.exifweb.web.security;

import image.exifweb.frameworks.hbm.HibernateAwareObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
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
 * Date: 11/23/13
 * Time: 1:51 AM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AuthFailureHandler implements AuthenticationFailureHandler {
	@Autowired
	private HibernateAwareObjectMapper hibernateAwareObjectMapper;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
	                                    AuthenticationException exception) throws IOException, ServletException {
		Map<String, String> success = new HashMap<String, String>();
		success.put("success", "false");
		success.put("error", "true");
		response.setContentType("application/json");
		hibernateAwareObjectMapper.writeValue(response.getOutputStream(), success);
	}
}
