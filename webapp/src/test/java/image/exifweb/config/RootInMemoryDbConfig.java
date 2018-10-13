package image.exifweb.config;

import image.exifweb.RootConfig;
import image.persistence.config.profiles.InMemoryDbProfile;
import org.junit.jupiter.api.Tag;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ContextConfiguration(classes = {RootConfig.class})
@TestPropertySource(properties = "hibernate.show_sql=true")
@InMemoryDbProfile
@Tag("junit5")
@Tag("inmemorydb")
@Tag("root")
public @interface RootInMemoryDbConfig {
}
