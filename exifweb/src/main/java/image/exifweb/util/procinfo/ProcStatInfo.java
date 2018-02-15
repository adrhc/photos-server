package image.exifweb.util.procinfo;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 12/17/13
 * Time: 8:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProcStatInfo implements ProcStatPercent, Serializable {
	private String percent;
	private String command;

	public ProcStatInfo(String percent, String command) {
		this.percent = percent;
		this.command = command;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getPercent() {
		return percent;
	}

	public void setPercent(String percent) {
		this.percent = percent;
	}

	@Override
	public String toString() {
		return "ProcStatInfo{" +
				"command='" + command + '\'' +
				", percent='" + percent + '\'' +
				'}';
	}
}
