package image.exifweb.web.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Adrian
 * Date: 11/23/13
 * Time: 5:00 PM
 * To change this template use File | Settings | File Templates.
 */
@WebSecurityComponent
public final class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
	@Override
	public void commence(final HttpServletRequest request,
			final HttpServletResponse response,
			final AuthenticationException authException) throws IOException {
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
	}
}
