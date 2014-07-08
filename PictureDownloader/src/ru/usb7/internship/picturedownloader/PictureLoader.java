package ru.usb7.internship.picturedownloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ru.usb7.internship.picturedownloader.PicrureLoaderResult.LoaderResult;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

public class PictureLoader extends AsyncTaskLoader<PicrureLoaderResult> {
	private static final String TAG = "MyTag";
	String fromUrl;
	File toFile;

	public PictureLoader(Context context, String fromUrl, File toFile) {
		super(context);
		Log.d(TAG, "PictureLoader");
		this.fromUrl = fromUrl;
		this.toFile = toFile;
	}

	@Override
	public PicrureLoaderResult loadInBackground() {
		Log.d(TAG, "loadInBackground");
		try {
			byte[] buffer = new byte[1024];
			URL url = new URL(fromUrl);
			HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
			httpUrlConnection.connect();

			String type = httpUrlConnection.getContentType();
			Log.d(TAG, "type: " + type);
			if (type.indexOf("image") == 0) {
				int fileLen = httpUrlConnection.getContentLength();
				if (fileLen < 0) {
					fileLen = Integer.MAX_VALUE;
				}
				InputStream in = url.openStream();
				int len;
				int loaded = 0;
				FileOutputStream fOut = new FileOutputStream(toFile);
				while ((len = in.read(buffer)) != -1) {
					fOut.write(buffer, 0, len);
					loaded += len;
					final int progress = 100 * loaded / fileLen;

					deliverResult(new PicrureLoaderResult(LoaderResult.LOADING, Integer.valueOf(progress)));
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				}
				fOut.close();
				in.close();
				return new PicrureLoaderResult(LoaderResult.LOADED, Integer.valueOf(100));
			} else {
				return new PicrureLoaderResult(LoaderResult.FAILED, Integer.valueOf(0));
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return new PicrureLoaderResult(LoaderResult.FAILED, Integer.valueOf(0));
		} catch (IOException e1) {
			e1.printStackTrace();
			return new PicrureLoaderResult(LoaderResult.FAILED, Integer.valueOf(0));
		}
	}

	@Override
	protected void onStartLoading() {
		Log.d(TAG, "onStartLoading");
	}
}