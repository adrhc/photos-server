package image.exifweb.apache;

import image.exifweb.config.RootInMemoryDbConfig;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@RootInMemoryDbConfig
class ApacheServiceTest {
	@Inject
	private ApplicationContext ac;
	@Inject
	private ApacheService apacheService;

	@Test
	void getAccessLogFile() {
		assertNotNull(this.apacheService.getAccessLogFile(), "getErrorLogFile is null");
	}

	@Test
	void getErrorLogFile() {
		assertNotNull(this.apacheService.getErrorLogFile(), "getErrorLogFile is null");
	}
}
