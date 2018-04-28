package image.exifweb.web.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

public class AuthUtil {
	public AuthData getAuthData() {
		AbstractAuthenticationToken auth = (AbstractAuthenticationToken)
				SecurityContextHolder.getContext().getAuthentication();
		return new AuthData(auth.getName(), getAuthorities());
	}

	private List<String> getAuthorities() {
		return SecurityContextHolder.getContext().getAuthentication()
				.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());
	}
}
