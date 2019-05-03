package image.exifweb.web.security;

import java.lang.annotation.*;

/**
 * Created by adr on 2/19/18.
 * <p>
 * I want to point that the classes annotated with this relate somehow to WebSecurityConfig.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebSecurityComponent {
	String value() default "";
}
