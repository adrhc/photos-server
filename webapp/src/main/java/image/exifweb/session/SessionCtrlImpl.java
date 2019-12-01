package image.exifweb.session;

import image.exifweb.web.security.AuthCheckResponse;
import image.exifweb.web.security.AuthData;
import image.exifweb.web.security.AuthUtil;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpSession;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 11/27/13
 * Time: 6:18 PM
 * To change this template use File | Settings | File Templates.
 */
@RestController
@RequestMapping("/json/session")
public class SessionCtrlImpl implements SessionCtrl {
	private AuthUtil authUtil = new AuthUtil();

	@Override
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public AuthCheckResponse getSessionData(WebRequest webRequest, HttpSession httpSession) {
		if (webRequest.checkNotModified(httpSession.getCreationTime())) {
			return null;
		}
		AuthData authData = this.authUtil.getAuthData();
		return new AuthCheckResponse(authData);
	}
}
