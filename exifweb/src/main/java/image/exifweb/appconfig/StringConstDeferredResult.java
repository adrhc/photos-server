package image.exifweb.appconfig;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import javax.inject.Inject;

/**
 * Created by adr on 10/27/16.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StringConstDeferredResult extends DeferredResult<String> implements Runnable {
	@Inject
	private ThreadPoolTaskExecutor asyncExecutor;
	private String string;

	public StringConstDeferredResult setString(String string) {
		this.string = string;
		asyncExecutor.execute(this);
		return this;
	}

	@Override
	public void run() {
		this.setResult(string);
	}
}
