package org.ddth.mobile.monitor.core;

public interface Reporter<T extends Report> {

	void report(T report);
}
