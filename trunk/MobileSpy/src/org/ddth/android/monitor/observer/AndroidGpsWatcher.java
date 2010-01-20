package org.ddth.android.monitor.observer;

import java.util.Date;

import org.ddth.android.monitor.core.AndroidEvent;
import org.ddth.mobile.monitor.core.Event;
import org.ddth.mobile.monitor.core.Reporter;
import org.ddth.mobile.monitor.report.GPS;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * @author khoanguyen
 *
 * @param <T>
 */
public class AndroidGpsWatcher extends AndroidWatcher {

	private static final String[] INTENTS = {};
	
	/**
	 * Time (in milliseconds) between 2 GPS logging
	 */
	private static final long GPS_LOGGING_INTERVAL = 120000L;
	
	/**
	 * Time (in milliseconds) to get location updated from Android device
	 */
	private static final long LOCATION_UPDATE_INTERVAL = 60000L;

	private LocationListener listener;

	public AndroidGpsWatcher(Reporter reporter) {
		setReporter(reporter);
	}
	
	@Override
	public String[] getIntents() {
		return INTENTS;
	}

	@Override
	public void start(Event dc) {
		super.start(dc);
		registerLocationListener((AndroidEvent) dc);
	}

	@Override
	public void stop(Event dc) {
		super.stop(dc);
		Context context = ((AndroidEvent) dc).getContext();
		LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		manager.removeUpdates(listener);
	}

	/**
	 * Register GPS location change events for periodically notified.
	 * 
	 * @param context
	 */
	private void registerLocationListener(final AndroidEvent dc) {
		Context context = dc.getContext();
		listener = new LocationListener() {
			private long lastUpdateTime = 0;
			
			public void onLocationChanged(Location location) {
				long now = System.currentTimeMillis();
				if (now - lastUpdateTime > GPS_LOGGING_INTERVAL) {
					getReporter().report(dc, getGPS(location));
					lastUpdateTime = now;
				}
			}

			public void onProviderDisabled(String provider) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {
			}
		};
		LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		manager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, LOCATION_UPDATE_INTERVAL, 0.0f, listener);
	}
	
	/**
	 * Extract GPS information from location object
	 * 
	 * @param location
	 * @return
	 */
	private GPS getGPS(Location location) {
		double lon = location.getLongitude();
		double lat = location.getLatitude();
		double speed = location.getSpeed();
		double dir = location.getBearing();
		Date now = new Date(location.getTime());
		return new GPS(lon, lat, speed, dir, now);
	}
}