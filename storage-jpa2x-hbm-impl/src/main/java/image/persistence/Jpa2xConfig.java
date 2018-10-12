package image.persistence;

import image.persistence.entity.Image;
import image.persistence.jpacustomizations.CustomJpaRepositoryImpl;
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
@ComponentScan(basePackages = {
		"image.persistence.repositories",
		"image.persistence.services"})
@EnableJpaRepositories(repositoryBaseClass = CustomJpaRepositoryImpl.class)
@EnableTransactionManagement
@Import({HibernatePropertiesConfig.class, DataSourceConfig.class})
public class Jpa2xConfig {
	/**
	 * LocalContainerEntityManagerFactoryBean implements PersistenceExceptionTranslator
	 * https://stackoverflow.com/questions/41801512/spring-how-exception-translation-works
	 */
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
