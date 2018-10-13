package image.exifweb.util.procinfo;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 12/18/13
 * Time: 3:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class FreeMInfo implements Serializable {
	private String totalMemory;
	private String usedMemory;
	private String usedMemoryPercent;
	private String usedMemoryPercentInt;
	private String usedSwap;
	private String totalSwap;
	private String usedSwapPercent;

	public FreeMInfo(String totalMemory, String usedMemory, String usedMemoryPercent,
			String usedMemoryPercentInt, String usedSwap, String totalSwap,
			String usedSwapPercent) {
		this.totalMemory = totalMemory;
		this.usedMemory = usedMemory;
		this.usedMemoryPercent = usedMemoryPercent;
		this.usedMemoryPercentInt = usedMemoryPercentInt;
		this.usedSwap = usedSwap;
		this.totalSwap = totalSwap;
		this.usedSwapPercent = usedSwapPercent;
	}

	public String getUsedMemoryPercentInt() {
		return usedMemoryPercentInt;
	}

	public void setUsedMemoryPercentInt(String usedMemoryPercentInt) {
		this.usedMemoryPercentInt = usedMemoryPercentInt;
	}

	public String getTotalMemory() {
		return totalMemory;
	}

	public void setTotalMemory(String totalMemory) {
		this.totalMemory = totalMemory;
	}

	public String getUsedMemory() {
		return usedMemory;
	}

	public void setUsedMemory(String usedMemory) {
		this.usedMemory = usedMemory;
	}

	public String getUsedMemoryPercent() {
		return usedMemoryPercent;
	}

	public void setUsedMemoryPercent(String usedMemoryPercent) {
		this.usedMemoryPercent = usedMemoryPercent;
	}

	public String getTotalSwap() {
		return totalSwap;
	}

	public void setTotalSwap(String totalSwap) {
		this.totalSwap = totalSwap;
	}

	public String getUsedSwap() {
		return usedSwap;
	}

	public void setUsedSwap(String usedSwap) {
		this.usedSwap = usedSwap;
	}

	public String getUsedSwapPercent() {
		return usedSwapPercent;
	}

	public void setUsedSwapPercent(String usedSwapPercent) {
		this.usedSwapPercent = usedSwapPercent;
	}
}
