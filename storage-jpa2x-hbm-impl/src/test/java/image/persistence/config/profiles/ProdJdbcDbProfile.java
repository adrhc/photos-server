package image.persistence.config.profiles;

import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ActiveProfiles({"prod-jdbc-ds", "jpa2x-hbm-impl"})
public @interface ProdJdbcDbProfile {
}
