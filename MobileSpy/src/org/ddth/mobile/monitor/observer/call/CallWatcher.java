package org.ddth.mobile.monitor.observer.call;

import org.ddth.mobile.monitor.core.WatcherAdapter;
import org.ddth.mobile.monitor.observer.call.CallReporter.Call;

public abstract class CallWatcher<T extends CallReporter> extends WatcherAdapter<T> {
	/**
	 * @param observable
	 * @return
	 */
	protected abstract Call parse(Object observable);
	
	@Override
	public void observed(Object observable) {
		Call call = parse(observable);
		if (call != null) {
			reporter.report(call);
		}
	}
}
