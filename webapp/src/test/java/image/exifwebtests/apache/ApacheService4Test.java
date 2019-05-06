package image.exifwebtests.apache;

import image.exifweb.apache.ApacheService;
import image.exifwebtests.config.RootInMemoryDbConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@RootInMemoryDbConfig
class ApacheService4Test {
	@Autowired
	private ApacheService apacheService;

	@Test
	void getAccessLogFile() {
		assertNotNull(this.apacheService.getAccessLogFile(), "getAccessLogFile is null");
	}

	@Test
	void getErrorLogFile() {
		assertNotNull(this.apacheService.getErrorLogFile(), "getErrorLogFile is null");
	}
}
