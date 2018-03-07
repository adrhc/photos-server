package image.persistence.repository.springconfig;

import image.persistence.HibernateConfig;
import image.persistence.repository.springconfig.profile.StagingJdbcDbProfile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by adr on 2/24/18.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ContextConfiguration(classes = {HibernateConfig.class})
@TestPropertySource(properties = "hibernate.show_sql=false")
@StagingJdbcDbProfile
public @interface HbmStagingJdbcDbConfig {
}
