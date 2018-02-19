package image.exifweb.web.config;

import image.exifweb.RootConfig;
import image.exifweb.WebConfig;
import image.exifweb.web.context.ContextLoaderListenerEx;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Created by adr on 2/14/18.
 */
public class WebApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class[]{RootConfig.class};
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[]{WebConfig.class};
	}

	@Override
	protected WebApplicationContext createServletApplicationContext() {
		ContextLoaderListenerEx.wac = super.createServletApplicationContext();
		return ContextLoaderListenerEx.wac;
	}

	@Override
	protected String[] getServletMappings() {
		return new String[]{"/app/*"};
	}
}
