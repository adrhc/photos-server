package image.photostests.junit4.testconfig;

import image.jpa2xtests.config.profiles.StageJdbcDbProfile;
import image.photos.PhotosConfig;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ContextConfiguration(classes = {PhotosConfig.class})
@TestPropertySource(properties = "hibernate.show_sql=false")
@StageJdbcDbProfile
public @interface PhotosStageJdbcDbConfig {
}
