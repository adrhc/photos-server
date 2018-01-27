package image.exifweb.web.reqexchandler;

import image.exifweb.sys.exception.RuntimeWithMsgExc;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	private MessageSource ms;

	@ExceptionHandler
	public ResponseEntity<Map<String, String>> handleRuntimeWithMsgExc(RuntimeWithMsgExc rwme) {
		Map<String, String> errInfo = new HashMap<String, String>(4, 1);
		errInfo.put("message", ms.getMessage(rwme.getMessageKey(), rwme.getArgs(), null));
		errInfo.put("success", "false");
		errInfo.put("error", "true");
		if (rwme.getMessage() != null) {
			errInfo.put("detailMessage", rwme.getMessage());
		}
		return new ResponseEntity<Map<String, String>>(errInfo, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler
	public ResponseEntity<Map<String, String>> handleException(Exception e) {
		logger.error(e.getMessage(), e);
		Map<String, String> errInfo = new HashMap<String, String>(3, 1);
		errInfo.put("message", e.getMessage());
		errInfo.put("stack trace", ExceptionUtils.getStackTrace(e));
		errInfo.put("success", "false");
		errInfo.put("error", "true");
		return new ResponseEntity<Map<String, String>>(errInfo, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
