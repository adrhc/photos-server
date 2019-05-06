package image.exifwebtests.apache;

import image.exifweb.apache.ApacheService;
import image.exifwebtests.config.RootInMemoryDbConfig;
import image.persistence.entitytests.IAppConfigSupplier;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@RootInMemoryDbConfig
@Slf4j
class ApacheServiceTest implements IAppConfigSupplier {
	@Inject
	private ApacheService apacheService;

	@BeforeAll
	void setup(@Autowired DataSource dataSource) throws SQLException {
		try (Connection conn = dataSource.getConnection()) {
			ScriptUtils.executeSqlScript(conn, new ClassPathResource("appconfig.sql"));
			ScriptUtils.executeSqlScript(conn, new ClassPathResource("album.sql"));
		}
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
