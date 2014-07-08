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
	String fromUrl;
	File toFile;

	public PictureLoader(Context context, String fromUrl, File toFile) {
		super(context);
		this.fromUrl = fromUrl;
		this.toFile = toFile;
		Log.e("123", "PictureLoader");
	}

	@Override
	public PicrureLoaderResult loadInBackground() {
		try {
			byte[] buffer = new byte[1024];
			URL url = new URL(fromUrl);
			HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
			httpUrlConnection.connect();

			// String type = httpUrlConnection.getContentType();
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
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return new PicrureLoaderResult(LoaderResult.LOADED, Integer.valueOf(100));
	}

	@Override
	protected void onStartLoading() {
		Log.e("123", "onStartLoading");
	}
}