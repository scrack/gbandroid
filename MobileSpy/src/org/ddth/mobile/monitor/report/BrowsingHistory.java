package org.ddth.mobile.monitor.report;

import java.util.List;

import org.ddth.mobile.monitor.core.Report;

public class BrowsingHistory implements Report {
	public HistoryUrl[] urls;

	public BrowsingHistory(List<HistoryUrl> urls) {
		this.urls = urls.toArray(new HistoryUrl[urls.size()]);
	}
}
