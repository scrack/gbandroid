package org.ddth.http.core;

import android.util.Log;

public class Logger {
	private static Logger logger = new Logger();
	
	public static Logger getDefault() {
		return logger;
	}

	public void error(String message, Throwable e) {
		Log.e("MobileSpyService", message, e);
	}

	public void debug(String message) {
		Log.d("MobileSpyService", message);
	}

	public void info(String message) {
		Log.i("MobileSpyService", message);
	}

	public void trace(String message) {
		Log.d("MobileSpyService", message);
	}

}
