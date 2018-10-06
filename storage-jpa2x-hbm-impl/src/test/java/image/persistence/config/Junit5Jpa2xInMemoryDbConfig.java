package image.persistence.config;

import image.persistence.Jpa2xConfig;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {Jpa2xConfig.class})
@TestPropertySource(properties = "hibernate.show_sql=false")
@ActiveProfiles({"in-memory-db", "jpa2x-hbm-impl"})
@Tag("junit5")
@Tag("jpa2x")
@Tag("inmemorydb")
public @interface Junit5Jpa2xInMemoryDbConfig {}
