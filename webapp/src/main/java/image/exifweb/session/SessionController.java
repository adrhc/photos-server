package image.exifweb.session;

import image.exifweb.web.security.AuthCheckResponse;
import image.exifweb.web.security.AuthData;
import image.exifweb.web.security.AuthUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpSession;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 11/27/13
 * Time: 6:18 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/json/session")
public class SessionController {
	private AuthUtil authUtil = new AuthUtil();

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public AuthCheckResponse getSessionData(WebRequest webRequest, HttpSession httpSession) {
		if (webRequest.checkNotModified(httpSession.getCreationTime())) {
			return null;
		}
		AuthData authData = this.authUtil.getAuthData();
		return new AuthCheckResponse(authData);
	}
}
