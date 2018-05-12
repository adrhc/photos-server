package image.persistence.repository.springconfig;

import image.persistence.HibernateConfig;
import image.persistence.repository.springprofile.StagingJdbcDbProfile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used for read/write disk DB tests.
 *
 * @HbmStagingJdbcDbConfig is a specialization @ContextConfiguration
 * as @StagingJdbcDbProfile is a specialization @ActiveProfiles
 * <p>
 * Created by adr on 2/24/18.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ContextConfiguration(classes = {HibernateConfig.class})
@TestPropertySource(properties = "hibernate.show_sql=false")
@StagingJdbcDbProfile
public @interface HbmStagingJdbcDbConfig {
}
