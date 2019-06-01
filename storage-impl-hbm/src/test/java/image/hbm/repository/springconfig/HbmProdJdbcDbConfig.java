package image.hbm.repository.springconfig;

import image.hbm.HibernateConfig;
import image.hbm.repository.springprofile.ProdJdbcDbProfile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used for readonly disk DB tests.
 *
 * @HbmProdJdbcDbConfig is a specialization of @ContextConfiguration
 * as @ProdJdbcDbProfile is a specialization of @ActiveProfiles
 * <p>
 * Created by adr on 2/24/18.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ContextConfiguration(classes = {HibernateConfig.class})
@TestPropertySource(properties = "hibernate.show_sql=false")
@ProdJdbcDbProfile
public @interface HbmProdJdbcDbConfig {
}
