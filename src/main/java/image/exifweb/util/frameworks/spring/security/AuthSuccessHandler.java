package image.exifweb.util.frameworks.spring.security;

import image.exifweb.util.frameworks.hibernate.HibernateAwareObjectMapper;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 11/22/13
 * Time: 9:49 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AuthSuccessHandler implements AuthenticationSuccessHandler {
	private static final Logger logger = LoggerFactory.getLogger(AuthSuccessHandler.class);
	@Autowired
	private HibernateAwareObjectMapper hibernateAwareObjectMapper;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
	                                    Authentication authentication) throws IOException, ServletException {
		logger.debug("Log on user: {}",
				SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		response.setContentType("application/json");
		Map<String, Object> success = prepareSessionData();
		success.put("success", "true");
		success.put("error", "false");
		hibernateAwareObjectMapper.writeValue(response.getOutputStream(), success);
	}

	public Map<String, Object> prepareSessionData() {
		Map<String, Object> success = new HashMap<>(3, 1);
		AbstractAuthenticationToken up = (AbstractAuthenticationToken)
				SecurityContextHolder.getContext().getAuthentication();
		success.put("userName", up.getName());
		success.put("authorities", getAuthorities());
		return success;
	}

	private Collection<String> getAuthorities() {
		BeanToPropertyValueTransformer transformer =
				new BeanToPropertyValueTransformer("authority");
		return CollectionUtils.collect(
				SecurityContextHolder.getContext().getAuthentication().getAuthorities(),
				transformer);
	}
}
