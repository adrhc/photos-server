package image.exifweb.web.config;

import image.exifweb.system.exception.RuntimeWithMsgExc;
import image.exifweb.web.security.AuthData;
import image.exifweb.web.security.AuthUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 11/17/13
 * Time: 8:14 PM
 * To change this template use File | Settings | File Templates.
 */
@ControllerAdvice
public class RequestExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(RequestExceptionHandler.class);
	@Autowired
	private MessageSource messageSource;
	private AuthUtil authUtil = new AuthUtil();

	@ExceptionHandler
	public ResponseEntity<Map<String, String>> handleRuntimeWithMsgExc(RuntimeWithMsgExc rwme) {
		Map<String, String> errInfo = new HashMap<String, String>(4, 1);
		errInfo.put("message", this.messageSource.getMessage(rwme.getMessageKey(), rwme.getArgs(), null));
		errInfo.put("success", "false");
		errInfo.put("error", "true");
		if (rwme.getMessage() != null) {
			errInfo.put("detailMessage", rwme.getMessage());
		}
		return new ResponseEntity<>(errInfo, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler
	public ResponseEntity<Map<String, String>> handleException(Exception e) {
		logger.error(e.getMessage(), e);
		Map<String, String> errInfo = new HashMap<String, String>(3, 1);
		errInfo.put("success", "false");
		errInfo.put("error", "true");
		processException(e, errInfo);
		return new ResponseEntity<>(errInfo, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private void processException(Exception e, Map<String, String> errInfo) {
		errInfo.put("message", e.getMessage());
		if (e instanceof AccessDeniedException) {
			AuthData authData = this.authUtil.getAuthData();
			if (authData == null) {
				errInfo.put("notLogged", "true");
			} else {
				errInfo.put("accessDenied", "true");
			}
		} else {
			errInfo.put("stack trace", ExceptionUtils.getStackTrace(e));
		}
	}
}
