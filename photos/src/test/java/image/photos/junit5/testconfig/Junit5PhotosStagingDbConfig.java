package image.photos.junit5.testconfig;

import image.photos.springconfig.PhotosStagingJdbcDbConfig;
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
@PhotosStagingJdbcDbConfig
@Tag("junit5")
@Tag("photos")
@Tag("staging")
public @interface Junit5PhotosStagingDbConfig {
}
