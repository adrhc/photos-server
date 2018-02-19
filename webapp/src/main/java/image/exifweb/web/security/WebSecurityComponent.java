package image.exifweb.web.security;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Created by adr on 2/19/18.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface WebSecurityComponent {
	String value() default "";
}
