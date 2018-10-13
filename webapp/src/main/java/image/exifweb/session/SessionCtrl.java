package image.exifweb.session;

import image.exifweb.web.security.AuthCheckResponse;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpSession;

public interface SessionCtrl {
	AuthCheckResponse getSessionData(WebRequest webRequest, HttpSession httpSession);
}
