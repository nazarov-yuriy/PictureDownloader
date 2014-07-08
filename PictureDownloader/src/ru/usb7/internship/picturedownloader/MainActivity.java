package ru.usb7.internship.picturedownloader;

import java.io.File;

import ru.usb7.internship.picturedownloader.PicrureLoaderResult.LoaderResult;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<PicrureLoaderResult> {
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
				getSupportLoaderManager().initLoader(LOADER_ID, null, activity).forceLoad();
			}
		};
		openClickListener = new View.OnClickListener() {
			public void onClick(View v) {
			}
		};
		setBtnDownload();
	}

	@Override
	protected void onDestroy() {
		File file = new File(getApplicationContext().getCacheDir(), pictureName);
		file.delete();
		super.onDestroy();
	}

	private void setBtnDownload() {
		final Button button = (Button) findViewById(R.id.buttonDownloadOpen);
		button.setText(getResources().getString(R.string.button_download_title));
		button.setOnClickListener(downloadClickListener);
	}

	private void setBtnOpen() {
		final Button button = (Button) findViewById(R.id.buttonDownloadOpen);
		button.setText(getResources().getString(R.string.button_open_title));
		button.setOnClickListener(openClickListener);
	}

	@Override
	public void onLoadFinished(Loader<PicrureLoaderResult> loader, PicrureLoaderResult data) {
		Log.e("123", "onLoadFinished");

		ProgressBar pb = (ProgressBar) findViewById(R.id.progressBarDownloading);
		pb.setProgress(data.loaderProgress);
		if (data.loaderResult == LoaderResult.LOADED) {
			TextView tv = (TextView) findViewById(R.id.textViewStatusValue);
			tv.setText("Loaded");
			Toast.makeText(getApplicationContext(), "loaded", Toast.LENGTH_SHORT).show();
			setBtnOpen();
		}
	}

	@Override
	public Loader<PicrureLoaderResult> onCreateLoader(int arg0, Bundle arg1) {
		File dir = new File(getCacheDir().getAbsolutePath());
		dir.mkdirs();
		File file = new File(dir, "picture.png");
		PictureLoader loader = new PictureLoader(this, getResources().getString(R.string.picture_url), file);
		return loader;
	}

	@Override
	public void onLoaderReset(Loader<PicrureLoaderResult> arg0) {
		// TODO Auto-generated method stub
	}
}