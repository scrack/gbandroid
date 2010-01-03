package org.ddth.mobile.monitor.observer.sms;

import org.ddth.mobile.monitor.core.WatcherAdapter;
import org.ddth.mobile.monitor.observer.sms.SmsReporter.SMS;

public abstract class SmsWatcher<T extends SmsReporter> extends WatcherAdapter<T> {
	/**
	 * @param observable
	 * @return
	 */
	protected abstract SMS parse(Object observable);
	
	@Override
	public void observed(Object observable) {
		SMS sms = parse(observable);
		if (sms != null) {
			reporter.report(sms);
		}
	}
}
