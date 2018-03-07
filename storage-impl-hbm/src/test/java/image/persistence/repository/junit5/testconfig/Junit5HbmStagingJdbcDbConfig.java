package image.persistence.repository.junit5.testconfig;

import image.persistence.repository.springconfig.HbmStagingJdbcDbConfig;
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
@HbmStagingJdbcDbConfig
@Tag("junit5")
@Tag("hbm")
@Tag("staging")
public @interface Junit5HbmStagingJdbcDbConfig {
}
