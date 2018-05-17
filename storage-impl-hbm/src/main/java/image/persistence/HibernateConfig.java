package image.persistence;

import image.persistence.entity.Image;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
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
@PropertySource("jdbc-datasource.properties")
@PropertySource("jndi-datasource.properties")
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
//@Import(HibernateCacheConfig.class)
@Import({HibernatePropertiesConfig.class, DataSourceConfig.class})
@ComponentScan(basePackageClasses = HibernateConfig.class,
		excludeFilters = @ComponentScan.Filter(Configuration.class))
public class HibernateConfig {
	private static final Logger logger = LoggerFactory.getLogger(HibernateConfig.class);

	@Bean
	public static PropertySourcesPlaceholderConfigurer
	propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Autowired
	@Bean
	public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
		logger.debug("begin");
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
	 * http://www.baeldung.com/hibernate-4-spring
	 *
	 * @return
	 */
	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}
}
