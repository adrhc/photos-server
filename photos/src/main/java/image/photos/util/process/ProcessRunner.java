package image.photos.util.process;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by adr on 2/19/18.
 */
@Component
public class ProcessRunner {
	public String getProcessOutput(ProcessBuilder processBuilder) throws IOException, InterruptedException {
		Process p = processBuilder.start();
		p.waitFor();
		InputStream is = p.getInputStream();
		String psOutput = IOUtils.toString(is, "UTF-8");
		IOUtils.closeQuietly(is);
		p.destroy();
		return psOutput;
	}
}
