package image.hbm.repository.springprofile;

import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used for read/write disk DB tests.
 * <p>
 * Created by adr on 2/24/18.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ActiveProfiles({"test-jdbc-ds", "hbm-impl"})
public @interface StageJdbcDbProfile {
}
