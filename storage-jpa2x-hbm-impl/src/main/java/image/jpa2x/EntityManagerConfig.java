package image.jpa2x;

import image.jpa2x.jpacustomizations.CustomJpaRepositoryImpl;
import image.persistence.entity.Image;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

@Profile("jpa2x-hbm-impl")
@Configuration
@EnableJpaRepositories(repositoryBaseClass = CustomJpaRepositoryImpl.class)
public class EntityManagerConfig {
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
}
