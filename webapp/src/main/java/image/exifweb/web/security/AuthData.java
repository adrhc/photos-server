package image.exifweb.web.security;

import java.io.Serializable;
import java.util.List;

public class AuthData implements Serializable {
	private final String userName;
	private final List<String> authorities;

	public AuthData(String userName, List<String> authorities) {
		this.userName = userName;
		this.authorities = authorities;
	}

	public String getUserName() {
		return this.userName;
	}

	public List<String> getAuthorities() {
		return this.authorities;
	}
}
