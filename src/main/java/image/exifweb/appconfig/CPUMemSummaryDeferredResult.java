package image.exifweb.appconfig;

import org.springframework.ui.Model;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 12/19/13
 * Time: 3:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class CPUMemSummaryDeferredResult extends DeferredResult<Model> implements Runnable {
	private Vector<CPUMemSummaryDeferredResult> asyncSubscribers;

	public CPUMemSummaryDeferredResult(Vector<CPUMemSummaryDeferredResult> asyncSubscribers) {
		super();
		this.asyncSubscribers = asyncSubscribers;
		this.asyncSubscribers.add(this);
		onCompletion(this);
	}

	@Override
	public void run() {
		asyncSubscribers.remove(this);
	}
}
