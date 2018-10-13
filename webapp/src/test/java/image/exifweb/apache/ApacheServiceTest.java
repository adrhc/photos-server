package image.exifweb.apache;

import image.exifweb.config.RootInMemoryDbConfig;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@NotThreadSafe
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
