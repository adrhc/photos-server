package image.exifweb.web.config;

import image.exifweb.RootConfig;
import image.exifweb.WebConfig;
import image.exifweb.web.context.ContextLoaderListenerEx;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.FrameworkServlet;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Created by adr on 2/14/18.
 */
public class WebApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
	/**
	 * 6.1.3 AbstractSecurityWebApplicationInitializer with Spring MVC
	 * <p>
	 * https://docs.spring.io/spring-security/site/docs/5.1.3.RELEASE/reference/htmlsingle/#abstractsecuritywebapplicationinitializer-with-spring-mvc
	 */
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
		System.out.println("creating ContextLoaderListenerEx.wac");
		ContextLoaderListenerEx.wac = super.createServletApplicationContext();
		return ContextLoaderListenerEx.wac;
	}

	@Override
	protected String[] getServletMappings() {
		System.out.println("Servlet mappings: /app/*");
		return new String[]{"/app/*"};
	}

	@Override
	protected FrameworkServlet createDispatcherServlet(WebApplicationContext servletAppContext) {
		System.out.println("configuring DispatcherServlet, e.g. throwExceptionIfNoHandlerFound=true");
		DispatcherServlet servlet = (DispatcherServlet) super.createDispatcherServlet(servletAppContext);
		servlet.setThrowExceptionIfNoHandlerFound(true);
		return servlet;
	}
}
