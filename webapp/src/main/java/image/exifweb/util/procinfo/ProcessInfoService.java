package image.exifweb.util.procinfo;

import image.exifweb.appconfig.CPUMemSummaryDeferredResult;
import image.photos.config.AppConfigService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 12/13/13
 * Time: 12:16 AM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ProcessInfoService {
	private static final Logger logger = LoggerFactory.getLogger(ProcessInfoService.class);
	private static final Pattern topCPUDetailPattern =
			Pattern.compile("(\\s+)(\\d+\\p{Punct}\\d+)(\\s+)(\\d+\\p{Punct}\\d+)(\\s+)([\\S]+)(\\s+)([\\S]+)");
	private static final Pattern psCPUDetailPattern =
			Pattern.compile("(\\s*)(\\d+\\p{Punct}\\d+)(\\s+)(.+)");
	private static final Pattern psMemDetailPattern =
			Pattern.compile("(\\s*)(\\d+)(\\s+)(\\d+\\p{Punct}\\d+)(\\s+)(.+)");
	private static final Pattern freeMemSummaryPattern1 =
			Pattern.compile("Mem:(\\s*)(\\d+)");
	private static final Pattern freeMemSummaryPattern2 =
			Pattern.compile("buffers/cache:(\\s*)(\\d+)");
	private static final Pattern freeMemSwapPattern3 =
			Pattern.compile("Swap:(\\s*)(\\d+)(\\s*)(\\d+)");
	private static final Pattern cgiCPUSummaryPattern =
			Pattern.compile("_CPU_utilization':'(\\d+)");
	private static final Pattern topCPUSummaryPattern =
			Pattern.compile("(\\d+\\p{Punct}\\d+)%id,");
	private static final ProcessBuilder freeMemCmdSummary =
			new ProcessBuilder("free", "-m");
	private static final ProcessBuilder topCmdCPUSummary =
			new ProcessBuilder("top", "-b", "-d", "0", "-n", "1", "-p", "99999");
	private static final ProcessBuilder psAxCmdCPUDetail = new ProcessBuilder(
			"ps", "ax", "--sort=-pcpu", "-o", "pcpu,comm");
	private static final ProcessBuilder psXCmdCPUDetail = new ProcessBuilder(
			"ps", "x", "--sort=-pcpu", "-o", "pcpu,comm");
	private static final ProcessBuilder topCmdCPUDetail = new ProcessBuilder(
			"top", "-bn1");
	private static final ProcessBuilder psCmdMemDetail = new ProcessBuilder(
			"ps", "ax", "--sort=-rss,pmem", "-o", "rss,pmem,comm");
	public final Vector<CPUMemSummaryDeferredResult> asyncSubscribers = new Vector<>();
	private URL cpuCGI;
	@Value("${httpd-admin.base}")
	private String httpdAdminBase;
	@Value("${memProcInfo.rss.mb}")
	private boolean memProcInfoRssMb;
	@Inject
	private AppConfigService appConfigService;

	/**
	 * E un job, vezi in xml.
	 *
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void syncCPUMemInfo() throws IOException, InterruptedException {
		synchronized (asyncSubscribers) {
			if (asyncSubscribers.isEmpty()) {
				return;
			}
//            StopWatch sw = new StopWatch();
//            sw.start("syncCPUMemInfo");
			ExtendedModelMap model = new ExtendedModelMap();
			if (appConfigService.getConfigBool("sync_cpu_mem_full")) {
				prepareProcMemFullStats(model);
			} else {
				prepareCPUMemSummary(model, null);
			}
			for (CPUMemSummaryDeferredResult subscriber : asyncSubscribers) {
				subscriber.setResult(model);
			}
//            sw.stop();
//            logger.debug(sw.prettyPrint());
		}
	}

	public void prepareProcMemFullStats(Model model) throws IOException, InterruptedException {
//		StopWatch sw = new StopWatch();
//		sw.start("prepareProcMemFullStats");
		List<ProcStatPercent> procStatInfos = getCPUDetailUsingTop();
		prepareCPUMemSummary(model, procStatInfos);
//        List<ProcStatPercent> procStatInfos = getCPUDetailUsingPsAx();
		List<ProcStatPercent> memProcInfos = getMemDetailUsingPs();
		int linesLimit = appConfigService.getConfigInteger("linux_process_status_lines_limit");
		if (linesLimit > 0 && procStatInfos.size() > linesLimit) {
			model.addAttribute("procStat", procStatInfos.subList(0, linesLimit));
		} else {
			model.addAttribute("procStat", procStatInfos);
		}
		if (linesLimit > 0 && memProcInfos.size() > linesLimit) {
			model.addAttribute("memStat", memProcInfos.subList(0, linesLimit));
		} else {
			model.addAttribute("memStat", memProcInfos);
		}
//        model.addAttribute("procTotal", sumProcStatPercent(procStatInfos));
//        model.addAttribute("memTotal", sumProcStatPercent(memProcInfos));
//		sw.stop();
//		logger.debug(sw.prettyPrint());
	}

	private float sumProcStatPercent(List<ProcStatPercent> procStatPercents) {
		float sum = 0;
		for (ProcStatPercent percent : procStatPercents) {
			sum += Float.parseFloat(percent.getPercent());
		}
		return sum;
	}

	public void prepareCPUMemSummary(Model model, List<ProcStatPercent> procStatInfos) throws IOException, InterruptedException {
//        StopWatch sw = new StopWatch();
//        sw.start("prepareCPUMemSummary");
		if (appConfigService.getConfigBool("cpu summary: use top (summary portion) command")) {
			model.addAttribute(getTopCPUUsage());
		} else if (appConfigService.getConfigBool("cpu summary: use nsa310 CGI")) {
			model.addAttribute("nsa310CgiCpuUsage", getNsa310CgiCpuUsage());
		} else if (appConfigService.getConfigBool("cpu summary: use sum on top command")) {
			if (procStatInfos == null) {
				procStatInfos = getCPUDetailUsingTop();
			}
			model.addAttribute("nsa310CgiCpuUsage",
					String.format("%.1f", sumProcStatPercent(procStatInfos)));
		} else if (appConfigService.getConfigBool("cpu summary: use sum on ps x command")) {
			procStatInfos = getCPUDetailUsingPsX();
			model.addAttribute("nsa310CgiCpuUsage",
					String.format("%.1f", sumProcStatPercent(procStatInfos)));
		} else if (appConfigService.getConfigBool("cpu summary: use sum on ps ax command")) {
			procStatInfos = getCPUDetailUsingPsAx();
			model.addAttribute("nsa310CgiCpuUsage",
					String.format("%.1f", sumProcStatPercent(procStatInfos)));
		}
		model.addAttribute(getMemSummaryUsingFreeM());
//        sw.stop();
//        logger.debug(sw.prettyPrint());
	}

	public TopCPUInfo getTopCPUUsage() throws IOException, InterruptedException {
		Process p = topCmdCPUSummary.start();
		p.waitFor();
		InputStream is = p.getInputStream();
		String psOutput = IOUtils.toString(is, "UTF-8");
		IOUtils.closeQuietly(is);
		p.destroy();
		Matcher matcher = topCPUSummaryPattern.matcher(psOutput);
		matcher.find();
		double cpu = 100.0 - Double.parseDouble(matcher.group(1));
		return new TopCPUInfo(String.format("%.1f", cpu), String.format("%.0f", cpu));
	}

	/**
	 * Returneaza ceva de genu:
	 * ({
	 * zyshdata0: [
	 * {'_CPU_utilization':'5 %'}
	 * ],
	 * errno0:0,
	 * errmsg0:'OK'
	 * })
	 *
	 * @return
	 * @throws IOException
	 */
	public String getNsa310CgiCpuUsage() throws IOException {
		InputStream is = cpuCGI.openConnection().getInputStream();
		String value = IOUtils.toString(is, "UTF-8");
		is.close();
		Matcher matcher = cgiCPUSummaryPattern.matcher(value);
		matcher.find();
		return matcher.group(1);
	}

	public List<String> getProcessesRunning(String[] commands) throws InterruptedException, IOException {
		List<String> params = new ArrayList<>();
		params.add("ps");
		for (String cmd : commands) {
			params.add("-C");
			params.add(cmd);
		}
		params.add("-o");
		params.add("command");
		ProcessBuilder ps = new ProcessBuilder(params);
		return getProcessesRunning(commands, ps);
	}

	public String getProcessRunning(String command) throws InterruptedException, IOException {
		ProcessBuilder ps = new ProcessBuilder(
				"ps",
				"-C", command, "-o", "command");
		List<String> commandsRunning = getProcessesRunning(new String[]{command}, ps);
		return commandsRunning.isEmpty() ? null : commandsRunning.get(0);
	}

	private List<String> getProcessesRunning(String[] commands, ProcessBuilder ps) throws InterruptedException, IOException {
		Process p = ps.start();
		p.waitFor();
		InputStream is = p.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		reader.readLine();// COMMAND column header
		List<String> commandsRunning = new ArrayList<>(commands.length);
		String line;
		while ((line = reader.readLine()) != null) {
			for (String cmd : commands) {
				if (line.contains(cmd)) {
					commandsRunning.add(cmd);
				}
			}
		}
		reader.close();
		p.destroy();
		return commandsRunning;
	}

	public void killSubtitlesExtractor() throws IOException, InterruptedException {
		killProcessForCmd("subtitles-extractor", "java");
	}

	public void killProcessForCmd(String cmdSubstring, String exactCommand)
			throws IOException, InterruptedException {
		logger.debug("BEGIN cmdSubstring = {}, exactCommand = {}", cmdSubstring, exactCommand);
		List<ProcessInfo> processInfos = getProcInfoForCommand(exactCommand);
		if (processInfos.isEmpty()) {
			logger.debug("END no processes");
			return;
		}
		List<String> killCommand = new ArrayList<String>();
		killCommand.add("kill");
		killCommand.add("-9");
		for (ProcessInfo processInfo : processInfos) {
			if (processInfo.getCmd().contains(cmdSubstring)) {
				killCommand.add(processInfo.getPid());
				logger.debug("will kill:\n{}", processInfo.toString());
			}
		}
		ProcessBuilder ps = new ProcessBuilder(killCommand);
		Process p = ps.start();
		p.waitFor();
		logger.debug("END");
	}

	private List<ProcessInfo> getProcInfoForCommand(String command)
			throws InterruptedException, IOException {
		logger.debug("BEGIN command = {}", command);
		ProcessBuilder ps = new ProcessBuilder(
				"ps", "-C", command, "-o", "pid,cmd");
		Process p = ps.start();
		p.waitFor();
		InputStream is = p.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		reader.readLine();// COMMAND column header
		List<ProcessInfo> processInfos = new ArrayList<ProcessInfo>();
		ProcessInfo processInfo;
		int spaceIdx;
		String line;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			spaceIdx = line.indexOf(' ');
			processInfo = new ProcessInfo(line.substring(0, spaceIdx), line.substring(spaceIdx + 1));
			processInfos.add(processInfo);
		}
		reader.close();
		p.destroy();
		return processInfos;
	}

	public List<ProcStatPercent> getCPUDetailUsingTop() throws InterruptedException, IOException {
		Process p = topCmdCPUDetail.start();
		p.waitFor();
		InputStream is = p.getInputStream();
		String psOutput = IOUtils.toString(is, "UTF-8");
		IOUtils.closeQuietly(is);
		p.destroy();
		List<ProcStatPercent> procStatInfos = new ArrayList<>();
		Matcher matcher = topCPUDetailPattern.matcher(psOutput);
		while (matcher.find()) {
			procStatInfos.add(new ProcStatInfo(matcher.group(2), matcher.group(8)));
		}
		return procStatInfos;
	}

	public List<ProcStatPercent> getCPUDetailUsingPsAx() throws InterruptedException, IOException {
		return getCPUDetailUsingPs(psAxCmdCPUDetail);
	}

	public List<ProcStatPercent> getCPUDetailUsingPsX() throws InterruptedException, IOException {
		return getCPUDetailUsingPs(psXCmdCPUDetail);
	}

	private List<ProcStatPercent> getCPUDetailUsingPs(ProcessBuilder processBuilder) throws InterruptedException, IOException {
		Process p = processBuilder.start();
		p.waitFor();
		InputStream is = p.getInputStream();
		String psOutput = IOUtils.toString(is, "UTF-8");
		IOUtils.closeQuietly(is);
		p.destroy();
		List<ProcStatPercent> procStatInfos = new ArrayList<>();
		Matcher matcher = psCPUDetailPattern.matcher(psOutput);
		while (matcher.find()) {
			procStatInfos.add(new ProcStatInfo(matcher.group(2), matcher.group(4)));
		}
		return procStatInfos;
	}

	public List<ProcStatPercent> getMemDetailUsingPs() throws InterruptedException, IOException {
		Process p = psCmdMemDetail.start();
		p.waitFor();
		InputStream is = p.getInputStream();
		String psOutput = IOUtils.toString(is, "UTF-8");
		IOUtils.closeQuietly(is);
		p.destroy();
		List<ProcStatPercent> memProcInfos = new ArrayList<ProcStatPercent>();
		Matcher matcher = psMemDetailPattern.matcher(psOutput);
		while (matcher.find()) {
			memProcInfos.add(new MemProcInfo(matcher.group(2),
					matcher.group(4), matcher.group(6), memProcInfoRssMb));
		}
		return memProcInfos;
	}

	public FreeMInfo getMemSummaryUsingFreeM() throws InterruptedException, IOException {
		Process p = freeMemCmdSummary.start();
		p.waitFor();
		InputStream is = p.getInputStream();
		String pOutput = IOUtils.toString(is, "UTF-8");
		IOUtils.closeQuietly(is);
		p.destroy();
		Matcher matcher = freeMemSummaryPattern1.matcher(pOutput);
		matcher.find();
		String totalMemoryS = matcher.group(2);
		double totalMemory = Double.parseDouble(totalMemoryS);
		matcher = freeMemSummaryPattern2.matcher(pOutput);
		matcher.find();
		String usedRAMS = matcher.group(2);
		matcher = freeMemSwapPattern3.matcher(pOutput);
		matcher.find();
		String totalSwap = matcher.group(2);
		String usedSwap = matcher.group(4);
		double usedRAM = Double.parseDouble(usedRAMS);
		double usedRAMPercent = 100.0 * usedRAM / totalMemory;
		double usedSwapPercent = 100.0 * Double.parseDouble(usedSwap) / Double.parseDouble(totalSwap);
		return new FreeMInfo(totalMemoryS, usedRAMS, String.format("%.1f", usedRAMPercent),
				String.format("%.0f", usedRAMPercent), usedSwap, totalSwap,
				String.format("%.1f", usedSwapPercent));
	}

	public String getPidByFile(String filePath) throws IOException {
		if (!new File(filePath).exists()) {
			return null;
		}
		return IOUtils.toString(new FileInputStream(filePath));
	}

	public String[] pidof(String cmd) throws IOException, InterruptedException {
		ProcessBuilder pidof = new ProcessBuilder("pidof", cmd);
		Process p = pidof.start();
		p.waitFor();
		InputStream is = p.getInputStream();
		String psOutput = IOUtils.toString(is, "UTF-8");
		IOUtils.closeQuietly(is);
		p.destroy();
		return psOutput.split("\\s+");
	}

	public String pgrep(String cmd, boolean checkEntireCommand) throws IOException, InterruptedException {
		ProcessBuilder pgrep;
		if (checkEntireCommand) {
			pgrep = new ProcessBuilder("pgrep", "-f", cmd);
		} else {
			pgrep = new ProcessBuilder("pgrep", cmd);
		}
		Process p = pgrep.start();
		p.waitFor();
		InputStream is = p.getInputStream();
		String psOutput = IOUtils.toString(is, "UTF-8");
		IOUtils.closeQuietly(is);
		p.destroy();
		return psOutput != null ? psOutput.trim() : null;
	}

	@PostConstruct
	public void postConstruct() {
		try {
			cpuCGI = new URL(httpdAdminBase +
					"cgi/cpu/?write=0&c0=configure%20terminal%20show%20cpu%20status");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public class ProcessInfo {
		private String pid;
		private String cmd;

		public ProcessInfo(String pid, String cmd) {
			this.pid = pid;
			this.cmd = cmd;
		}

		public String getPid() {
			return pid;
		}

		public void setPid(String pid) {
			this.pid = pid;
		}

		public String getCmd() {
			return cmd;
		}

		public void setCmd(String cmd) {
			this.cmd = cmd;
		}

		@Override
		public String toString() {
			return "ProcessInfo{" +
					"pid='" + pid + '\'' +
					", cmd='" + cmd + '\'' +
					'}';
		}
	}
}
