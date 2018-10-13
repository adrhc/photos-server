package image.persistence.repository.junit5.springconfig;

import exifweb.util.junit.NestedPerClass;
import image.persistence.repository.springconfig.HbmStagingJdbcDbConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@HbmStagingJdbcDbConfig
@NestedPerClass
public @interface Junit5HbmStagingJdbcDbNestedConfig {
}
