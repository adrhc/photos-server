package image.exifwebtests.apache;

import image.exifweb.apache.ApacheService;
import image.exifwebtests.config.RootInMemoryDbConfig;
import image.jpa2x.repositories.AppConfigRepository;
import image.persistence.entitytests.IAppConfigSupplier;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * By default, the Surefire Plugin will automatically include all test classes with the following wildcard patterns:
 * - includes all of its subdirectories and all Java filenames that start with "Test".
 * - includes all of its subdirectories and all Java filenames that end with "Test".
 * - includes all of its subdirectories and all Java filenames that end with "Tests".
 * - includes all of its subdirectories and all Java filenames that end with "TestCase".
 */
@RootInMemoryDbConfig
@Slf4j
@Disabled
class ApacheService3Test implements IAppConfigSupplier {
	@Autowired
	private ApacheService apacheService;
	@Autowired
	private AppConfigRepository appConfigRepository;

	@BeforeAll
	void setup() {
		ApacheService3Test.log.debug("");
		this.appConfigRepository.persist(entityAppConfigOf("apache-log-dir", "/home/adr/apps/log"));
	}

	@Test
	void getAccessLogFile() {
		assertNotNull(this.apacheService.getAccessLogFile(), "getAccessLogFile is null");
	}

	@Test
	void getErrorLogFile() {
		assertNotNull(this.apacheService.getErrorLogFile(), "getErrorLogFile is null");
	}
}
