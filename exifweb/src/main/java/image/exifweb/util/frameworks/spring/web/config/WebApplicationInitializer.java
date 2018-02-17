package image.exifweb.util.frameworks.spring.web.config;

import image.exifweb.RootConfig;
import image.exifweb.WebConfig;
import image.exifweb.WebSecurityConfig;
import image.exifweb.util.frameworks.spring.web.context.ContextLoaderListenerEx;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.support.AbstractDispatcherServletInitializer;

/**
 * Created by adr on 2/14/18.
 */
public class WebApplicationInitializer extends AbstractDispatcherServletInitializer {
//	@Override
//	protected Filter[] getServletFilters() {
//		return new Filter[]{new DelegatingFilterProxy("springSecurityFilterChain")};
//	}

	@Override
	protected WebApplicationContext createRootApplicationContext() {
		AnnotationConfigWebApplicationContext acwac = new AnnotationConfigWebApplicationContext();
		acwac.register(RootConfig.class, WebSecurityConfig.class);
		ContextLoaderListenerEx.wac = acwac;
		return acwac;
//		XmlWebApplicationContext cxt = new XmlWebApplicationContext();
//		ContextLoaderListenerEx.wac = cxt;
//		cxt.setConfigLocations("classpath:spring/root-*.xml", "classpath*:/org/springframework/jdbc/support/sql-error-codes.xml");
//		return cxt;
	}

	@Override
	protected WebApplicationContext createServletApplicationContext() {
		AnnotationConfigWebApplicationContext acwac = new AnnotationConfigWebApplicationContext();
		acwac.register(WebConfig.class);
		return acwac;
	}

	@Override
	protected String[] getServletMappings() {
		return new String[]{"/app/*"};
	}
}
