package org.ddth.android.spy;

import org.ddth.android.monitor.observer.AndroidSmsWatcher;
import org.ddth.mobile.monitor.core.Watcher;
import org.ddth.mobile.monitor.observer.sms.SmsReporter;

public class SmsSpy {

	public void spy() {
		AndroidSmsWatcher<SmsSpyReporter> watcher = new AndroidSmsWatcher<SmsSpyReporter>();
		watcher.register(new SmsSpyReporter());
		watcher.watch(null, Watcher.START_MONITORING);
	}
}

class SmsSpyReporter extends SmsReporter {

	@Override
	public void report(SMS sms) {
		
	}
}