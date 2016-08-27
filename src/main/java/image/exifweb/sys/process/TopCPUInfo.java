package image.exifweb.sys.process;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 12/19/13
 * Time: 1:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class TopCPUInfo implements Serializable {
	private String cpuPercent;
	private String cpuPercentInt;

	public TopCPUInfo(String cpuPercent, String cpuPercentInt) {
		this.cpuPercent = cpuPercent;
		this.cpuPercentInt = cpuPercentInt;
	}

	public String getCpuPercent() {
		return cpuPercent;
	}

	public void setCpuPercent(String cpuPercent) {
		this.cpuPercent = cpuPercent;
	}

	public String getCpuPercentInt() {
		return cpuPercentInt;
	}

	public void setCpuPercentInt(String cpuPercentInt) {
		this.cpuPercentInt = cpuPercentInt;
	}

	@Override
	public String toString() {
		return "TopCPUInfo{" +
				"cpuPercent='" + cpuPercent + '\'' +
				", cpuPercentInt='" + cpuPercentInt + '\'' +
				'}';
	}
}
