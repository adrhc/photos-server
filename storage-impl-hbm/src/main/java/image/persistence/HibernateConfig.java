package image.persistence;

import com.zaxxer.hikari.HikariDataSource;
import image.persistence.entity.Image;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @EnableTransactionManagement applicable
 * everywhere where is included (even transitive)!
 * <p>
 * Created by adr on 2/17/18.
 */
@Configuration
//@Import(HibernateCacheConfig.class)
@PropertySource(value = {"classpath:/jdbc-datasource.properties",
		"classpath*:/jdbc-datasource-overridden.properties"},
		ignoreResourceNotFound = true)
@PropertySource(value = {"classpath:/jndi-datasource.properties",
		"classpath*:/jndi-datasource-overridden.properties"},
		ignoreResourceNotFound = true)
@PropertySource(value = {"classpath:/hibernate.properties",
		"classpath*:/hibernate-overridden.properties"},
		ignoreResourceNotFound = true)
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
@ComponentScan(basePackageClasses = HibernateConfig.class,
		excludeFilters = @ComponentScan.Filter(Configuration.class))
public class HibernateConfig {
	@Autowired
	private Environment env;

	@Bean
	public static PropertySourcesPlaceholderConfigurer
	propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Autowired
	@Bean
	public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
		HibernateTransactionManager txManager = new HibernateTransactionManager();
		txManager.setSessionFactory(sessionFactory);
		return txManager;
	}

	@Autowired
	@Bean
	public LocalSessionFactoryBean sessionFactory(
			DataSource dataSource, @Qualifier("hibernateProperties") Properties hibernateProperties) {
		LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
		sessionFactoryBean.setDataSource(dataSource);
		sessionFactoryBean.setPackagesToScan(Image.class.getPackage().getName());
		sessionFactoryBean.setHibernateProperties(hibernateProperties);
		return sessionFactoryBean;
	}

	/**
	 * SQLErrorCodeSQLExceptionTranslator (uses sql-error-codes.xml) -> for jdbc only?
	 * <p>
	 * <jee:jndi-lookup id="dataSource" jndi-name="${jndi.name}" />
	 * <p>
	 * In tomcat's context.xml define: <Resource ... />
	 */
	@Profile("jndi-ds")
	@Bean
	public DataSource jndiDataSource(@Value("${jndi.name}") String jndiName) {
		JndiDataSourceLookup lookup = new JndiDataSourceLookup();
		return lookup.getDataSource(jndiName);
	}

	/**
	 * When using same name (e.g. dataSource) for jdbc and jndi datasources
	 * though they have different @Profile still won't work (none will be found).
	 */
	@Profile("jdbc-ds")
	@Bean
	public DataSource jdbcDataSource(@Value("${jdbc.url}") String jdbcUrl,
	                                 @Value("${jdbc.userName}") String userName,
	                                 @Value("${jdbc.password}") String password,
	                                 @Value("${jdbc.minimumIdle}") int minimumIdle,
	                                 @Value("${jdbc.maximumPoolSize}") int maximumPoolSize) {
		HikariDataSource ds = new HikariDataSource();
		ds.setJdbcUrl(jdbcUrl);
		ds.setUsername(userName);
		ds.setPassword(password);
		ds.setAutoCommit(false);
		ds.setMinimumIdle(minimumIdle);
		ds.setMaximumPoolSize(maximumPoolSize);
		return ds;
	}

	@Profile("in-memory-db")
	@Bean
	public DataSource inMemoryDataSource(@Value("${ramdb.jdbc.driverClass}") String driverClass,
	                                     @Value("${ramdb.jdbc.url}") String jdbcUrl,
	                                     @Value("${ramdb.jdbc.userName}") String userName,
	                                     @Value("${ramdb.jdbc.password}") String password) {
		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setDriverClassName(driverClass);
		ds.setUrl(jdbcUrl);
		if (StringUtils.hasText(userName)) {
			ds.setUsername(userName);
			if (StringUtils.hasText(password)) {
				ds.setPassword(password);
			}
		}
		return ds;
	}

	/**
	 * http://www.baeldung.com/hibernate-4-spring
	 *
	 * @return
	 */
	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	@Profile("in-memory-db")
	@Bean("hibernateProperties")
	public Properties hibernatePropertiesForInMemoryDb() {
		return new Properties() {
			{
				setProperty("hibernate.dialect", env.getProperty("ramdb.hibernate.dialect"));

				// for hbm < 4x
//				setProperty("net.sf.ehcache.cacheManagerName",
//						env.getProperty("net.sf.ehcache.cacheManagerName.in_memory_db"));
				// solution for hbm >= 4x
//				setProperty("net.sf.ehcache.configurationResourceName",
//						env.getProperty("net.sf.ehcache.configurationResourceName.in_memory_db"));

				setProperty("hibernate.hbm2ddl.auto",
						env.getProperty("ramdb.hibernate.hbm2ddl.auto"));
				addCommonHbmProps(this);
			}
		};
	}

	@Profile({"jdbc-ds", "jndi-ds"})
	@Bean("hibernateProperties")
	public Properties hibernatePropertiesForJdbcDs() {
		return new Properties() {
			{
//				setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
				setProperty("hibernate.dialect.storage_engine",
						env.getProperty("hibernate.dialect.storage_engine"));

				// for hbm < 4x
//				setProperty("net.sf.ehcache.cacheManagerName",
//						env.getProperty("net.sf.ehcache.cacheManagerName.jdbc_ds"));
				// solution for hbm >= 4x
//				setProperty("net.sf.ehcache.configurationResourceName",
//						env.getProperty("net.sf.ehcache.configurationResourceName.jdbc_ds"));

				addCommonHbmProps(this);
			}
		};
	}

	private void addCommonHbmProps(Properties properties) {
		properties.setProperty("hibernate.jdbc.batch_size",
				env.getProperty("hibernate.jdbc.batch_size"));
		properties.setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
		properties.setProperty("hibernate.format_sql", env.getProperty("hibernate.format_sql"));
		properties.setProperty("hibernate.validator.autoregister_listeners",
				env.getProperty("hibernate.validator.autoregister_listeners"));

		// http://www.baeldung.com/hibernate-second-level-cache => for hibernate 5.x
		// http://docs.jboss.org/hibernate/orm/4.3/manual/en-US/html_single/#performance-cache
		// properties.setProperty("hibernate.generate_statistics", "true");
		// properties.setProperty("hibernate.cache.use_structured_entries", "true");

		properties.setProperty("hibernate.cache.use_second_level_cache",
				env.getProperty("hibernate.cache.use_second_level_cache"));
		properties.setProperty("hibernate.cache.use_query_cache",
				env.getProperty("hibernate.cache.use_query_cache"));
		properties.setProperty("hibernate.cache.region.factory_class",
				env.getProperty("hibernate.cache.region.factory_class"));
//		properties.setProperty("net.sf.ehcache.configurationResourceName",
//				env.getProperty("net.sf.ehcache.configurationResourceName"));

		// properties.setProperty("hibernate.hbm2ddl.auto", "update");
		// properties.setProperty("hibernate.id.new_generator_mappings", "true");
		// properties.setProperty("hibernate.current_session_context_class", "jta");
		// properties.setProperty("javax.persistence.validation.mode", "");

	}
}
