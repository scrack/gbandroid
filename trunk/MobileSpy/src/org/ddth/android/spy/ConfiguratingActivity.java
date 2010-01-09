package org.ddth.android.spy;

import java.io.IOException;

import org.ddth.android.spy.reporter.SpyReporter;
import org.ddth.android.spy.reporter.SpyReporter.ResponseHandler;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConfiguratingActivity extends Activity implements ResponseHandler {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen_login_once);
		Button login = (Button) findViewById(R.id.login);
		final EditText username = (EditText) findViewById(R.id.username);
		final EditText password = (EditText) findViewById(R.id.password);
		final SharedPreferences settings = getSharedPreferences(SpyService.APPLICATION_TAG, MODE_PRIVATE);
		username.setText(settings.getString(SpyService.USERNAME_FIELD, ""));
		password.setText(settings.getString(SpyService.PASSWORD_FIELD, ""));
		
		login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Prohibit continuous clicking on login button. 
				findViewById(R.id.login).setEnabled(false);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString(SpyService.USERNAME_FIELD, username.getText().toString());
				editor.putString(SpyService.PASSWORD_FIELD, password.getText().toString());
				editor.commit();
				SpyReporter.getSpyLogger().setAuthCredentials(
						username.getText().toString(), password.getText().toString());
				SpyReporter.getSpyLogger().login(ConfiguratingActivity.this);
			}
		});

		Button cancel = (Button) findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hide();
			}
		});
	}

	/**
	 * Hide (destroy) this activity.
	 */
	private void hide() {
		moveTaskToBack(true);
		finish();
	}

	public void onResponse(final String body) throws IOException {
		// Because we have to update some UI components, we should execute it
		// within the UI thread
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// Re-enable login button
				findViewById(R.id.login).setEnabled(true);
				// If something occurred, notify that error to screen.
				// Otherwise, hide the Activity screen.
				if (body != null) {
					Toast.makeText(ConfiguratingActivity.this, body, Toast.LENGTH_LONG).show();
				}
				else {
					hide();
				}
			}
		});
	}
}