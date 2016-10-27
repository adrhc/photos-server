package image.exifweb.appconfig;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.ContextLoaderListenerEx;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * Created by adr on 10/27/16.
 */
public class ConstantDeferredResult<T> extends DeferredResult<T> implements Runnable {
    private T result;

    public ConstantDeferredResult<T> setResultThenRun(T result) {
        if (this.result != null) {
            throw new RuntimeException("Constant result already set!");
        }
        this.result = result;
        ContextLoaderListenerEx.wac.getBean(ThreadPoolTaskExecutor.class).execute(this);
        return this;
    }

    @Override
    public void run() {
        this.setResult(result);
    }
}
