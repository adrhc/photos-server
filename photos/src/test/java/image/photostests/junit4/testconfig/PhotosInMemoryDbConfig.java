package image.photostests.junit4.testconfig;

import image.jpa2xtests.config.profiles.InMemoryDbProfile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by adr on 3/2/18.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ContextConfiguration(classes = PhotosTestConfig.class)
@TestPropertySource(properties = "hibernate.show_sql=false")
@InMemoryDbProfile
public @interface PhotosInMemoryDbConfig {
}
