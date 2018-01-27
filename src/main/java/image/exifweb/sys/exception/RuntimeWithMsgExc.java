package image.exifweb.sys.exception;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 11/17/13
 * Time: 8:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class RuntimeWithMsgExc extends RuntimeException {
	private String messageKey;
	private Object[] args;

	public RuntimeWithMsgExc(Exception e, String messageKey) {
		super(e.getMessage(), e);
		this.messageKey = messageKey;
	}

	public RuntimeWithMsgExc(Exception e, String messageKey, Object... args) {
		this(e, messageKey);
		this.args = args;
	}

	public RuntimeWithMsgExc(String messageKey) {
		super();
		this.messageKey = messageKey;
	}

	public RuntimeWithMsgExc(String messageKey, Object... args) {
		this(messageKey);
		this.args = args;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}
}
