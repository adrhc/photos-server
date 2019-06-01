package image.exifwebtests.config;

import image.exifweb.RootConfig;
import image.exifweb.WebConfig;
import image.exifwebtests.apache.DBInitConfiguration;
import image.jpa2xtests.config.profiles.InMemoryDbProfile;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextHierarchy({
		@ContextConfiguration(classes = {RootConfig.class, DBInitConfiguration.class}),
		@ContextConfiguration(classes = {WebConfig.class})
})
@WebAppConfiguration
@TestPropertySource(properties = "hibernate.show_sql=false")
@InMemoryDbProfile
@Tag("junit5")
@Tag("inmemorydb")
@Tag("webapp")
public @interface WebInMemoryDbFilledConfig {
}
