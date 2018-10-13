package image.persistence.repository.junit5.springconfig;

import image.persistence.repository.springconfig.HbmInMemoryDbConfig;
import org.junit.jupiter.api.Nested;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@HbmInMemoryDbConfig
@Nested
public @interface Junit5HbmInMemoryDbNestedConfig {
}
