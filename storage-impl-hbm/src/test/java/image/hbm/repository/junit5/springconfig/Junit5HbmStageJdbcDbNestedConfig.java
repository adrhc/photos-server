package image.hbm.repository.junit5.springconfig;

import exifweb.util.junit.NestedPerClass;
import image.hbm.repository.springconfig.HbmStageJdbcDbConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@HbmStageJdbcDbConfig
@NestedPerClass
public @interface Junit5HbmStageJdbcDbNestedConfig {
}
