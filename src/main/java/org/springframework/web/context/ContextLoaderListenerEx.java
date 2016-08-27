package org.springframework.web.context;

import javax.servlet.ServletContextEvent;

/**
 * Created by IntelliJ IDEA.
 * User: adrian.petre
 * Date: 9/3/12
 * Time: 4:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class ContextLoaderListenerEx extends ContextLoaderListener {
	public static WebApplicationContext wac;

	public void contextInitialized(ServletContextEvent event) {
		//System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
		//System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
		super.contextInitialized(event);
		wac = getCurrentWebApplicationContext();
		System.out.println("END ContextLoaderListenerEx.contextInitialized");
	}
}
