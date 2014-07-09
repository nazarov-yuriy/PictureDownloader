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
				ProgressBar pb = (ProgressBar) findViewById(R.id.progressBarDownloading);
				pb.setVisibility(View.VISIBLE);
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
		final PicrureLoaderResult fdata = data;
		runOnUiThread(new Runnable() {
			public void run() {

				Log.d(TAG, "onLoadFinished");
				TextView tv = (TextView) findViewById(R.id.textViewStatusValue);
				ProgressBar pb = (ProgressBar) findViewById(R.id.progressBarDownloading);

				if (fdata.loaderResult == LoaderResult.LOADED) {
					tv.setText(getResources().getString(R.string.status_loaded));
					setBtnOpen();
					pb.setVisibility(View.INVISIBLE);
					Log.d(TAG, "tv1 " + tv + " th " + android.os.Process.myTid());
				} else if (fdata.loaderResult == LoaderResult.FAILED) {
					tv.setText(getResources().getString(R.string.status_idle));
					Toast.makeText(getApplicationContext(),
							getResources().getString(R.string.downloading_failed_toast), Toast.LENGTH_SHORT).show();
					setBtnDownload();
					pb.setVisibility(View.INVISIBLE);
					Log.d(TAG, "tv2 " + tv + " th " + android.os.Process.myTid());
				} else {
					Log.d(TAG, "tv31 " + tv + " th " + android.os.Process.myTid());
					tv.setText(getResources().getString(R.string.status_loading));
					pb.setProgress(fdata.loaderProgress);
					Log.d(TAG, "tv32 " + tv + " th " + android.os.Process.myTid());
				}
			}
		});
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