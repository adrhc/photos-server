package image.photos.springconfig;

import image.persistence.HibernateConfig;
import image.persistence.repository.springprofile.StagingJdbcDbProfile;
import image.photos.PhotosConfig;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ContextConfiguration(classes = {HibernateConfig.class, PhotosConfig.class})
@TestPropertySource(properties = "hibernate.show_sql=true")
@StagingJdbcDbProfile
public @interface PhotosStagingJdbcDbConfig {
}
