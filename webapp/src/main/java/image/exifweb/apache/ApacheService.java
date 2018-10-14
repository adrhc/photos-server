package image.exifweb.apache;

import image.photos.config.AppConfigService;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

/**
 * Created by adrian.petre on 18-06-2014.
 */
@Service
public class ApacheService {
	@Inject
	private AppConfigService appConfigService;
	@Value("${apache.access.log.prefix}")
	private String apacheAccessLogPrefix;
	@Value("${apache.error.log.prefix}")
	private String apacheErrorLogPrefix;

	public File getAccessLogFile() {
		return getLogFile(this.apacheAccessLogPrefix);
	}

	public File getErrorLogFile() {
		return getLogFile(this.apacheErrorLogPrefix);
	}

	private File getLogFile(final String fileNamePrefix) {
		File dir = new File(this.appConfigService.getConfig("apache-log-dir"));
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(fileNamePrefix);
			}
		});
		if (files == null) {
			return null;
		}
		Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
		return files[0];
	}
}
