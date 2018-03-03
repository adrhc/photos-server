package image.persistence.repository.junit5;

import image.persistence.repository.springtestconfig.InMemoryDbTestConfig;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(SpringExtension.class)
@InMemoryDbTestConfig
@Tag("junit5")
@Tag("hbm")
@Tag("inmemorydb")
@Nested
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public @interface Junit5HbmInMemoryDbNestedConfig {
}
