package image.photos.util.process;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by adr on 2/19/18.
 */
@Component
public class ProcessRunner {
	public String getProcessOutput(ProcessBuilder processBuilder) throws IOException, InterruptedException {
		Process p = processBuilder.start();
		p.waitFor();
		try (InputStream is = p.getInputStream()) {
			return IOUtils.toString(is, StandardCharsets.UTF_8);
		} finally {
			p.destroy();
		}
	}
}
