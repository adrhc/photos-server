package image.jpa2xtests.config;

import image.jpa2x.Jpa2xConfig;
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
@ContextConfiguration(classes = {Jpa2xConfig.class})
@TestPropertySource(properties = "hibernate.show_sql=false")
@InMemoryDbProfile
@Tag("junit5")
@Tag("jpa2x")
@Tag("inmemorydb")
public @interface Junit5Jpa2xInMemoryDbConfig {}
