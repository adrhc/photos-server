package image.jpa2x;

import org.springframework.context.annotation.*;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;

@Profile("jpa2x-hbm-impl")
@Configuration
@ComponentScan
@EnableTransactionManagement
@Import({HibernatePropertiesConfig.class, DataSourceConfig.class})
@PropertySource("classpath:photos-jpa2x.properties")
public class Jpa2xConfig {
	@Bean
	public PlatformTransactionManager transactionManager(
			EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager txManager = new JpaTransactionManager();
		txManager.setEntityManagerFactory(entityManagerFactory);
		return txManager;
	}
}
