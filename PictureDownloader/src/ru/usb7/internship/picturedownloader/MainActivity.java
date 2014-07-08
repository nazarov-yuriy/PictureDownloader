package ru.usb7.internship.picturedownloader;

import java.io.File;

import ru.usb7.internship.picturedownloader.PicrureLoaderResult.LoaderResult;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<PicrureLoaderResult> {
	private static final String TAG = "MyTag";
	private static final int LOADER_ID = 1;
	private static String pictureName = "logo.png";
	private static OnClickListener openClickListener;
	private static OnClickListener downloadClickListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getSupportLoaderManager().initLoader(LOADER_ID, null, this);

		final MainActivity activity = this;
		downloadClickListener = new View.OnClickListener() {
			public void onClick(View v) {
				Log.d(TAG, "onClick downloadClickListener");
				getSupportLoaderManager().initLoader(LOADER_ID, null, activity).forceLoad();
				v.setEnabled(false);
			}
		};
		openClickListener = new View.OnClickListener() {
			public void onClick(View v) {
				Log.d(TAG, "onClick openClickListener");
				Uri contentUri = FileProvider.getUriForFile(getApplicationContext(),
						"ru.usb7.internship.picturedownloader.fileprovider", // formatting
						new File(getApplicationContext().getFilesDir(), pictureName));

				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
				intent.setDataAndType(contentUri, "image/png");
				startActivity(intent);
			}
		};
		setBtnDownload();
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		File file = new File(getApplicationContext().getFilesDir(), pictureName);
		file.delete();
		super.onDestroy();
	}

	private void setBtnDownload() {
		Log.d(TAG, "setBtnDownload");
		final Button button = (Button) findViewById(R.id.buttonDownloadOpen);
		button.setText(getResources().getString(R.string.button_download_title));
		button.setOnClickListener(downloadClickListener);
		button.setEnabled(true);
	}

	private void setBtnOpen() {
		Log.d(TAG, "setBtnOpen");
		final Button button = (Button) findViewById(R.id.buttonDownloadOpen);
		button.setText(getResources().getString(R.string.button_open_title));
		button.setOnClickListener(openClickListener);
		button.setEnabled(true);
	}

	@Override
	public void onLoadFinished(Loader<PicrureLoaderResult> loader, PicrureLoaderResult data) {
		Log.d(TAG, "onLoadFinished");
		View v = findViewById(R.id.textViewStatusValue);
		TextView tv = (TextView) findViewById(R.id.textViewStatusValue);
		ProgressBar pb = (ProgressBar) findViewById(R.id.progressBarDownloading);

		pb.setProgress(data.loaderProgress);

		if (data.loaderResult == LoaderResult.LOADED) {
			tv.setText(getResources().getString(R.string.status_loaded));
			Toast.makeText(getApplicationContext(), "loaded", Toast.LENGTH_SHORT).show();
			setBtnOpen();
		} else if (data.loaderResult == LoaderResult.FAILED) {
			tv.setText(getResources().getString(R.string.status_idle));
			Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();
			setBtnOpen();
		} else {
			//tv.setText(getResources().getString(R.string.status_loading));
		}
	}

	@Override
	public Loader<PicrureLoaderResult> onCreateLoader(int arg0, Bundle arg1) {
		Log.d(TAG, "onCreateLoader");
		File dir = new File(getFilesDir().getAbsolutePath());
		dir.mkdirs();
		File file = new File(dir, pictureName);
		PictureLoader loader = new PictureLoader(this, getResources().getString(R.string.picture_url), file);
		return loader;
	}

	@Override
	public void onLoaderReset(Loader<PicrureLoaderResult> arg0) {
		Log.d(TAG, "onLoaderReset");
		// TODO Auto-generated method stub
	}
}