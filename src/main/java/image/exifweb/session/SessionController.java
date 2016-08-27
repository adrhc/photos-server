package image.exifweb.session;

import image.exifweb.web.security.AuthSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

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
    @Autowired
    private AuthSuccessHandler authSuccessHandler;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Map<String, Object> getSessionData(WebRequest webRequest, HttpSession httpSession) {
        if (webRequest.checkNotModified(httpSession.getCreationTime())) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("success", "true");
        map.put("error", "false");
        map.putAll(authSuccessHandler.prepareSessionData());
        return map;
    }
}
