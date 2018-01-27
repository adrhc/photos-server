package image.exifweb.appconfig;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.ui.Model;
import org.springframework.web.context.ContextLoaderListenerEx;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Vector;

/**
 * See also: ProcessInfoService.syncCPUMemInfo
 *
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 12/19/13
 * Time: 3:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class CPUMemSummaryDeferredResult extends DeferredResult<Model> implements Runnable {
    private Vector<CPUMemSummaryDeferredResult> asyncSubscribers;

    public CPUMemSummaryDeferredResult(Vector<CPUMemSummaryDeferredResult> asyncSubscribers) {
        this.asyncSubscribers = asyncSubscribers;
        this.asyncSubscribers.add(this);
        ContextLoaderListenerEx.wac.getBean(ThreadPoolTaskExecutor.class).execute(this);
    }

    @Override
    public void run() {
        asyncSubscribers.remove(this);
    }
}
