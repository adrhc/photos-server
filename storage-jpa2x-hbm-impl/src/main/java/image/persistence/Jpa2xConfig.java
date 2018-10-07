package image.persistence;

import image.persistence.entity.Image;
import image.persistence.jpacustomizations.CustomRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Profile("jpa2x-hbm-impl")
@Configuration
@ComponentScan(excludeFilters = {
		@ComponentScan.Filter(Configuration.class),
		@ComponentScan.Filter(type = FilterType.REGEX,
				pattern = "image.persistence.repository.*RepositoryImpl")
})
@EnableJpaRepositories(repositoryBaseClass = CustomRepositoryImpl.class)
@EnableTransactionManagement
@Import({HibernatePropertiesConfig.class, DataSourceConfig.class})
public class Jpa2xConfig {
	private static final Logger logger = LoggerFactory.getLogger(Jpa2xConfig.class);

	@Bean
	public static PropertySourcesPlaceholderConfigurer
	propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			DataSource dataSource, @Qualifier("hibernateProperties") Properties jpaProperties) {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setPackagesToScan(Image.class.getPackage().getName());
		factory.setDataSource(dataSource);
		factory.setJpaProperties(jpaProperties);
		return factory;
	}

	@Bean
	public PlatformTransactionManager transactionManager(
			EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager txManager = new JpaTransactionManager();
		txManager.setEntityManagerFactory(entityManagerFactory);
		return txManager;
	}
}
