package ru.usb7.internship.picturedownloader;

import java.io.File;

import ru.usb7.internship.picturedownloader.PicrureLoaderResult.LoaderResult;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
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

		downloadClickListener = new View.OnClickListener() {
			public void onClick(View button) {
				getSupportLoaderManager().getLoader(LOADER_ID).forceLoad();
				button.setEnabled(false);
			}
		};
		openClickListener = new View.OnClickListener() {
			public void onClick(View v) {
				Uri contentUri = FileProvider.getUriForFile(getApplicationContext(),
						"ru.usb7.internship.picturedownloader.fileprovider", // formatting
						new File(getFilesDir(), pictureName));

				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
				intent.setDataAndType(contentUri, "image/*");
				startActivity(intent);
			}
		};
		setBtnDownload();
	}

	private void setBtnDownload() {
		final Button button = (Button) findViewById(R.id.buttonDownloadOpen);
		button.setText(getResources().getString(R.string.button_download_title));
		button.setOnClickListener(downloadClickListener);
		button.setEnabled(true);
	}

	private void setBtnOpen() {
		final Button button = (Button) findViewById(R.id.buttonDownloadOpen);
		button.setText(getResources().getString(R.string.button_open_title));
		button.setOnClickListener(openClickListener);
		button.setEnabled(true);
	}

	@Override
	public void onLoadFinished(final Loader<PicrureLoaderResult> loader, final PicrureLoaderResult data) {
		if (!Looper.getMainLooper().equals(Looper.myLooper())) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					onLoadFinished(loader, data);
				}
			});
		} else {
			TextView tv = (TextView) findViewById(R.id.textViewStatusValue);
			ProgressBar pb = (ProgressBar) findViewById(R.id.progressBarDownloading);

			if (data.loaderResult == LoaderResult.LOADED) {
				tv.setText(getResources().getString(R.string.status_loaded));
				pb.setVisibility(View.INVISIBLE);
				setBtnOpen();
			} else if (data.loaderResult == LoaderResult.FAILED) {
				tv.setText(getResources().getString(R.string.status_idle));
				pb.setVisibility(View.INVISIBLE);
				setBtnDownload();
				Toast.makeText(this, getResources().getString(R.string.downloading_failed_toast), Toast.LENGTH_SHORT)
						.show();
			} else {
				tv.setText(getResources().getString(R.string.status_loading));
				findViewById(R.id.progressBarDownloading).setVisibility(View.VISIBLE);
				pb.setProgress(data.loaderProgress);
			}
		}
	}

	@Override
	public Loader<PicrureLoaderResult> onCreateLoader(int arg0, Bundle arg1) {
		File dir = new File(getFilesDir().getAbsolutePath());
		dir.mkdirs();
		File file = new File(dir, pictureName);
		PictureLoader loader = new PictureLoader(this, getResources().getString(R.string.picture_url), file);
		return loader;
	}

	@Override
	public void onLoaderReset(Loader<PicrureLoaderResult> arg0) {
		// TODO Auto-generated method stub
	}
}