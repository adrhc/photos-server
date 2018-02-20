package image.persistence;

import com.zaxxer.hikari.HikariDataSource;
import image.persistence.entity.Image;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @EnableTransactionManagement applicable
 * everywhere where is included (even transitive)!
 * <p>
 * Created by adr on 2/17/18.
 */
@Configuration
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
	@Value("${jndi.name}")
	private String jndiName;
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
	public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
		LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
		localSessionFactoryBean.setDataSource(dataSource);
		localSessionFactoryBean.setPackagesToScan(Image.class.getPackage().getName());
		localSessionFactoryBean.setHibernateProperties(hibernateProperties());
		return localSessionFactoryBean;
	}

	/**
	 * SQLErrorCodeSQLExceptionTranslator (uses sql-error-codes.xml) -> for jdbc only?
	 * <p>
	 * <jee:jndi-lookup id="dataSource" jndi-name="${jndi.name}" />
	 * <p>
	 * In tomcat's context.xml define: <Resource ... />
	 */
	@Profile("!jdbc-datasource")
	@Bean
	public DataSource jndiDataSource() {
		JndiDataSourceLookup lookup = new JndiDataSourceLookup();
		return lookup.getDataSource(jndiName);
	}

	/**
	 * When using same name (e.g. dataSource) for jdbc and jndi datasources
	 * though they have different @Profile still won't work (none will be found).
	 */
	@Profile("jdbc-datasource")
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

	/**
	 * http://www.baeldung.com/hibernate-4-spring
	 *
	 * @return
	 */
	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	private Properties hibernateProperties() {
		return new Properties() {
			{
				setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
				setProperty("hibernate.jdbc.batch_size",
						env.getProperty("hibernate.jdbc.batch_size"));
				setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
				setProperty("hibernate.format_sql", env.getProperty("hibernate.format_sql"));
				setProperty("hibernate.validator.autoregister_listeners",
						env.getProperty("hibernate.validator.autoregister_listeners"));

				// http://www.baeldung.com/hibernate-second-level-cache => for hibernate 5.x
				// http://docs.jboss.org/hibernate/orm/4.3/manual/en-US/html_single/#performance-cache
				// setProperty("hibernate.generate_statistics", "true");
				// setProperty("hibernate.cache.use_structured_entries", "true");

				setProperty("hibernate.cache.use_second_level_cache",
						env.getProperty("hibernate.cache.use_second_level_cache"));
				setProperty("hibernate.cache.use_query_cache",
						env.getProperty("hibernate.cache.use_query_cache"));
				setProperty("hibernate.cache.region.factory_class",
						env.getProperty("hibernate.cache.region.factory_class"));

				// setProperty("hibernate.hbm2ddl.auto", "update");
				// setProperty("hibernate.id.new_generator_mappings", "true");
				// setProperty("hibernate.current_session_context_class", "jta");
				// setProperty("javax.persistence.validation.mode", "");
			}
		};
	}
}
