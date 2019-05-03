package image.exifwebtests.config;

import image.exifweb.RootConfig;
import image.jpa2xtests.config.profiles.InMemoryDbProfile;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {RootConfig.class})
@TestPropertySource(properties = "hibernate.show_sql=true")
@InMemoryDbProfile
@Tag("junit5")
@Tag("inmemorydb")
@Tag("root")
public @interface RootInMemoryDbConfig {
}
