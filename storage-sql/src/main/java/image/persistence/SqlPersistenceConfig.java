package image.persistence;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by adr on 2/18/18.
 */
@Configuration
@Import(HibernateConfig.class)
@ComponentScan(basePackageClasses = SqlPersistenceConfig.class,
		excludeFilters = @ComponentScan.Filter(Configuration.class))
public class SqlPersistenceConfig {
}
