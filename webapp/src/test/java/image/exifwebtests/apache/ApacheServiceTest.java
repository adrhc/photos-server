package image.exifwebtests.apache;

import image.exifweb.apache.ApacheService;
import image.exifwebtests.config.RootInMemoryDbConfig;
import image.jpa2x.repositories.AppConfigRepository;
import image.persistence.entitytests.IAppConfigSupplier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@RootInMemoryDbConfig
class ApacheServiceTest implements IAppConfigSupplier {
	@Inject
	private ApacheService apacheService;
	@Inject
	private AppConfigRepository appConfigRepository;

	@BeforeAll
	void beforeAll() {
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
