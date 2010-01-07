package org.ddth.android.monitor.observer;

import java.util.Date;

import org.ddth.android.monitor.core.AndroidDC;
import org.ddth.mobile.monitor.core.DC;
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
	private static final long GPS_LOGGING_INTERVAL = 900000L;
	
	/**
	 * Time (in milliseconds) to get location updated from Android device
	 */
	private static final long LOCATION_UPDATE_INTERVAL = 60000L;

	private LocationListener listener;

	
	@Override
	public String[] getIntents() {
		return INTENTS;
	}

	@Override
	public void start(DC dc) {
		super.start(dc);
		registerLocationListener(((AndroidDC) dc).getContext());
	}

	@Override
	public void stop(DC dc) {
		super.stop(dc);
		Context context = ((AndroidDC) dc).getContext();
		LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		manager.removeUpdates(listener);
	}
	
	protected GPS getReport(DC dc, Object observable) {
		GPS gps = null;
		if (observable instanceof Location) {
			Location location = (Location)observable;
			double lon = location.getLongitude();
			double lat = location.getLatitude();
			double speed = location.getSpeed();
			double dir = location.getBearing();
			Date now = new Date(location.getTime());
			gps = new GPS(lon, lat, speed, dir, now);
		}
		return gps;
	}

	/**
	 * Register GPS location change events for periodically notified.
	 * 
	 * @param context
	 */
	private void registerLocationListener(Context context) {
		listener = new LocationListener() {
			private long lastUpdateTime = 0;
			
			public void onLocationChanged(Location location) {
				long now = System.currentTimeMillis();
				if (now - lastUpdateTime > GPS_LOGGING_INTERVAL) {
					observed(null, location);
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
}