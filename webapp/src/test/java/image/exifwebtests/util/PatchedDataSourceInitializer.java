package image.exifwebtests.util;

import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.jdbc.datasource.init.UncategorizedScriptException;
import org.springframework.lang.Nullable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * see org.springframework.jdbc.datasource.init.DataSourceInitializer
 * difference: databasePopulator, databaseCleaner run in a transactional context
 */
@Slf4j
@Setter
@Accessors(fluent = true)
public class PatchedDataSourceInitializer implements InitializingBean, DisposableBean {
	private boolean commit = true;
	private DataSource dataSource;
	private PlatformTransactionManager transactionManager;
	@Nullable
	private DatabasePopulator databasePopulator;
	@Nullable
	private DatabasePopulator databaseCleaner;

	@Override
	public void afterPropertiesSet() {
		new TransactionTemplate(this.transactionManager).execute((ts) -> {
			this.execute(this.databasePopulator);
			return null;
		});
	}

	@Override
	public void destroy() {
		new TransactionTemplate(this.transactionManager).execute((ts) -> {
			this.execute(this.databaseCleaner);
			return null;
		});
	}

	private void execute(@Nullable DatabasePopulator populator) {
		Assert.state(this.dataSource != null, "DataSource must be set");
		if (populator == null) {
			return;
		}
		try {
			Connection connection = DataSourceUtils.getConnection(this.dataSource);
			populator.populate(connection);
		} catch (ScriptException ex) {
			throw ex;
		} catch (Throwable ex) {
			throw new UncategorizedScriptException("Failed to execute database script", ex);
		}
		Connection connection = DataSourceUtils.getConnection(this.dataSource);
		if (this.commit) {
			try {
				connection.commit();
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}
		DataSourceUtils.releaseConnection(connection, this.dataSource);
	}
}
