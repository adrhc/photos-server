package image.photos.junit5.config.testconfig;

import image.photos.springtestconfig.InMemoryDbPhotosTestConfig;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(SpringExtension.class)
@InMemoryDbPhotosTestConfig
@Tag("junit5")
@Tag("hbm")
@Tag("inmemorydb")
public @interface Junit5InMemoryDbPhotosTestConfig {
}
