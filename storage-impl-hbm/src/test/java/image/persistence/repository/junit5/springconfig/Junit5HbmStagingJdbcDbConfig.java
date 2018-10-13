package image.persistence.repository.junit5.springconfig;

import image.persistence.repository.springconfig.HbmStagingJdbcDbConfig;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Junit5HbmStagingJdbcDbConfig is a junit5 variant of @HbmStagingJdbcDbConfig
 * Its purpose is to add junit5 tags (@Tag).
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@HbmStagingJdbcDbConfig
@Tag("junit5")
@Tag("hbm")
@Tag("staging")
public @interface Junit5HbmStagingJdbcDbConfig {
}
