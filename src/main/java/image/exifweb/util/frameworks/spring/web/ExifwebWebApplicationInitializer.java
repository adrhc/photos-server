package image.exifweb.util.frameworks.spring.web;

import image.exifweb.util.frameworks.spring.web.context.ContextLoaderListenerEx;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.support.AbstractDispatcherServletInitializer;

import javax.servlet.Filter;

/**
 * Created by adr on 2/14/18.
 */
public class ExifwebWebApplicationInitializer extends AbstractDispatcherServletInitializer {
	@Override
	protected Filter[] getServletFilters() {
		return new Filter[]{new DelegatingFilterProxy("springSecurityFilterChain")};
	}

	@Override
	protected WebApplicationContext createServletApplicationContext() {
		XmlWebApplicationContext cxt = new XmlWebApplicationContext();
		cxt.setConfigLocation("/WEB-INF/dispatcher-servlet.xml");
		return cxt;
	}

	@Override
	protected String[] getServletMappings() {
		return new String[]{"/app/*"};
	}

	@Override
	protected WebApplicationContext createRootApplicationContext() {
		XmlWebApplicationContext cxt = new XmlWebApplicationContext();
		ContextLoaderListenerEx.wac = cxt;
		cxt.setConfigLocations("classpath:spring/root-*.xml", "classpath*:/org/springframework/jdbc/support/sql-error-codes.xml");
//		cxt.setConfigLocations("classpath:spring/root-*.xml");
		return cxt;
	}
}
