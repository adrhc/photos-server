package image.exifweb.web.security;

import lombok.Data;

@Data
public class AuthCheckResponse extends AuthData {
	public static final AuthCheckResponse FAILED_AUTHENTICATION = new AuthCheckResponse();
	private final boolean success;
	private final boolean error;

	public AuthCheckResponse() {
		super(null, null);
		this.success = false;
		this.error = true;
	}

	public AuthCheckResponse(AuthData authData) {
		super(authData.getUserName(), authData.getAuthorities());
		this.success = true;
		this.error = false;
	}
}
