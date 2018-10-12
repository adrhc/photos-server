package image.photos.springconfig;

import image.persistence.repository.springprofile.ProdJdbcDbProfile;
import image.photos.PhotosConfig;
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
@ContextConfiguration(classes = {PhotosConfig.class})
@TestPropertySource(properties = "hibernate.show_sql=true")
@ProdJdbcDbProfile
public @interface PhotosProdJdbcDbConfig {
}
