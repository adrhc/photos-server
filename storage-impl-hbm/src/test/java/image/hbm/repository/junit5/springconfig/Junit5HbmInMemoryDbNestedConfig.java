package image.hbm.repository.junit5.springconfig;

import exifweb.util.junit.NestedPerClass;
import image.hbm.repository.springconfig.HbmInMemoryDbConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@HbmInMemoryDbConfig
@NestedPerClass
public @interface Junit5HbmInMemoryDbNestedConfig {
}
