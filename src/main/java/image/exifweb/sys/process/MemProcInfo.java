package image.exifweb.sys.process;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 12/17/13
 * Time: 8:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class MemProcInfo implements ProcStatPercent, Serializable {
    private static final DecimalFormat df = new DecimalFormat("#.#");
    private String rss;
    private String percent;
    private String command;

    public MemProcInfo(String rss, String percent, String command, boolean memProcInfoRssMb) {
        if (memProcInfoRssMb) {
            this.rss = df.format(Double.valueOf(rss) / 1024);
        } else {
            this.rss = rss;
        }
        this.percent = percent;
        this.command = command;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getRss() {
        return rss;
    }

    public void setRss(String rss) {
        this.rss = rss;
    }

    @Override
    public String toString() {
        return "MemProcInfo{" +
            "rss='" + rss + '\'' +
            ", percent='" + percent + '\'' +
            ", command='" + command + '\'' +
            '}';
    }
}
