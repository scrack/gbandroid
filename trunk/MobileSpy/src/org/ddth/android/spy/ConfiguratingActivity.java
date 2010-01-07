package org.ddth.android.spy;

import org.ddth.android.spy.SpyReporter.ContentHandler;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConfiguratingActivity extends Activity implements ContentHandler {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen_login_once);
		Button login = (Button) findViewById(R.id.login);
		final EditText username = (EditText) findViewById(R.id.username);
		final EditText password = (EditText) findViewById(R.id.password);
		final SharedPreferences settings = getSharedPreferences(SpyReporter.APPLICATION_TAG, MODE_PRIVATE);
		username.setText(settings.getString(SpyReporter.USERNAME_FIELD, ""));
		password.setText(settings.getString(SpyReporter.PASSWORD_FIELD, ""));
		
		login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Prohibit continuous clicking on login button. 
				findViewById(R.id.login).setEnabled(false);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString(SpyReporter.USERNAME_FIELD, username.getText().toString());
				editor.putString(SpyReporter.PASSWORD_FIELD, password.getText().toString());
				editor.commit();
				SpyReporter.login(ConfiguratingActivity.this, username.getText().toString(), password.getText().toString());
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

	private void hide() {
		moveTaskToBack(true);
		finish();
	}

	@Override
	public void handle(final String text) {
		// Enable login button
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// This must be run within UI thread
				findViewById(R.id.login).setEnabled(true);
			}
		});
		
		if (text != null) {
			// Notify error which is specified in the given text
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// This must be run within UI thread
					Toast.makeText(ConfiguratingActivity.this, text, Toast.LENGTH_LONG).show();
				}
			});
			return;
		}
		// Successfully sign in. Hide the Activity screen.
		hide();
	}
}
