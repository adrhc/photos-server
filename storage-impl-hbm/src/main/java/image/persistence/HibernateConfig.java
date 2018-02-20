package image.persistence;

import com.zaxxer.hikari.HikariDataSource;
import image.persistence.entity.Image;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
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
@PropertySource("classpath:/jndi-datasource.properties")
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
@ComponentScan(basePackageClasses = HibernateConfig.class,
		excludeFilters = @ComponentScan.Filter(Configuration.class))
public class HibernateConfig {
	@Value("${jndi.name}")
	private String jndiName;

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

	@Bean
	public LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
		localSessionFactoryBean.setDataSource(dataSource());
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
	@Profile({"!test*", "!jdbc-datasource"})
	@Bean
	public DataSource dataSource() {
		JndiDataSourceLookup lookup = new JndiDataSourceLookup();
		return lookup.getDataSource(jndiName);
	}

	@Profile("jdbc-datasource")
	@Bean
	public DataSource dataSource(@Value("${jdbc.url}") String jdbcUrl,
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
				setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
				setProperty("hibernate.jdbc.batch_size", "20");
				setProperty("hibernate.show_sql", "true");
				setProperty("hibernate.format_sql", "true");
				setProperty("hibernate.validator.autoregister_listeners", "false");

				// http://www.baeldung.com/hibernate-second-level-cache => for hibernate 5.x
				// http://docs.jboss.org/hibernate/orm/4.3/manual/en-US/html_single/#performance-cache
				// setProperty("hibernate.generate_statistics", "true");
				// setProperty("hibernate.cache.use_structured_entries", "true");

				setProperty("hibernate.cache.use_second_level_cache", "true");
				setProperty("hibernate.cache.use_query_cache", "true");
				setProperty("hibernate.cache.region.factory_class",
						"org.hibernate.cache.ehcache.EhCacheRegionFactory");

				// setProperty("hibernate.hbm2ddl.auto", "update");
				// setProperty("hibernate.id.new_generator_mappings", "true");
				// setProperty("hibernate.current_session_context_class", "jta");
				// setProperty("javax.persistence.validation.mode", "");
			}
		};
	}
}
