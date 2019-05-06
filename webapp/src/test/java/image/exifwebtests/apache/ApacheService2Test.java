package image.exifwebtests.apache;

import image.exifweb.apache.ApacheService;
import image.exifwebtests.config.RootInMemoryDbConfig;
import image.persistence.entitytests.IAppConfigSupplier;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@RootInMemoryDbConfig
@Slf4j
@Disabled
class ApacheService2Test implements IAppConfigSupplier {
	@Inject
	private ApacheService apacheService;

	/**
	 * should conflict with ApacheService1Test
	 */
	@BeforeAll
	void setup(@Autowired DataSource dataSource,
			@Autowired PlatformTransactionManager transactionManager) {
		ApacheService2Test.log.debug("");
		new TransactionTemplate(transactionManager).execute((ts) -> {
			try (Connection conn = dataSource.getConnection()) {
				ScriptUtils.executeSqlScript(conn, new ClassPathResource("appconfig.sql"));
				ScriptUtils.executeSqlScript(conn, new ClassPathResource("album.sql"));
				conn.commit();
			} catch (SQLException e) {
				ApacheService2Test.log.error(e.getMessage(), e);
			}
			return null;
		});
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
