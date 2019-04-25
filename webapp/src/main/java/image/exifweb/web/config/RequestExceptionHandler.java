package image.exifweb.web.config;

import image.exifweb.exception.RuntimeWithMsgExc;
import image.exifweb.web.security.AuthData;
import image.exifweb.web.security.AuthUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 11/17/13
 * Time: 8:14 PM
 * To change this template use File | Settings | File Templates.
 */
@RestControllerAdvice
public class RequestExceptionHandler extends ResponseEntityExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(RequestExceptionHandler.class);
	@Inject
	private MessageSource messageSource;
	private AuthUtil authUtil = new AuthUtil();

	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public Map<String, String> runtimeWithMsgExc(RuntimeWithMsgExc rwme) {
		Map<String, String> errInfo = emptyError();
		errInfo.put("message", this.messageSource.getMessage(rwme.getMessageKey(), rwme.getArgs(), null));
		if (rwme.getMessage() != null) {
			errInfo.put("detailMessage", rwme.getMessage());
		}
		return errInfo;
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public Map<String, String> accessDeniedException(AccessDeniedException e) {
		Map<String, String> errInfo = emptyError();
		errInfo.put("message", e.getMessage());
		AuthData authData = this.authUtil.getAuthData();
		if (authData == null) {
			errInfo.put("notLogged", "true");
		} else {
			errInfo.put("accessDenied", "true");
		}
		return errInfo;
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public Map<String, String> handleAnyException(Exception e) {
		logger.error(e.getMessage(), e);
		Map<String, String> errInfo = emptyError();
		errInfo.put("message", e.getMessage());
		errInfo.put("stack trace", ExceptionUtils.getStackTrace(e));
		return errInfo;
	}

	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(
			NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		logger.error(ex.getMessage(), ex);
		Map<String, String> error = emptyError();
		error.put("notFound", "true");
		error.put("request", request.getDescription(false));
		return handleExceptionInternal(ex, error, headers, status, request);
	}

	@Override
	protected ResponseEntity<Object> handleTypeMismatch(
			TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		logger.error(ex.getMessage(), ex);
		Map<String, String> error = emptyError();
		error.put("typeMismatch", "true");
		error.put("request", request.getDescription(false));
		return handleExceptionInternal(ex, error, headers, status, request);
	}

	private Map<String, String> emptyError() {
		Map<String, String> errInfo = new HashMap<>();
		errInfo.put("success", "false");
		errInfo.put("error", "true");
		return errInfo;
	}
}
