package image.exifweb.web.config;

import image.exifweb.RootConfig;
import image.exifweb.WebSecurityConfig;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

/**
 * Created by adr on 2/18/18.
 */
public class SecurityWebApplicationInitializer
		extends AbstractSecurityWebApplicationInitializer {
	public SecurityWebApplicationInitializer() {
		super(RootConfig.class, WebSecurityConfig.class);
	}
}
