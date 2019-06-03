package image.exifweb.web.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContextEvent;

/**
 * Created by IntelliJ IDEA.
 * User: adrian.petre
 * Date: 9/3/12
 * Time: 4:38 PM
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
public class ContextLoaderListenerEx extends ContextLoaderListener {
	public static WebApplicationContext wac;

	@Override
	public void contextInitialized(ServletContextEvent event) {
		//System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
		//System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
		super.contextInitialized(event);
		wac = getCurrentWebApplicationContext();
		log.info("END ContextLoaderListenerEx.contextInitialized");
	}
}
