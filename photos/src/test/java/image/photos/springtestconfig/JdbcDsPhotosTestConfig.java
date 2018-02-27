package image.photos.springtestconfig;

import image.persistence.repository.springtestconfig.springprofile.ProdJdbcDsActivated;
import image.photos.TestPhotosConfig;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by adr on 2/24/18.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ContextConfiguration(classes = {TestPhotosConfig.class})
@TestPropertySource(properties = "hibernate.show_sql=false")
@ProdJdbcDsActivated
public @interface JdbcDsPhotosTestConfig {
}
