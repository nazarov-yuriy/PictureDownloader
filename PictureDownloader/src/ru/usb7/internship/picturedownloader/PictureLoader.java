package ru.usb7.internship.picturedownloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import ru.usb7.internship.picturedownloader.PicrureLoaderResult.LoaderResult;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public class PictureLoader extends AsyncTaskLoader<PicrureLoaderResult> {
	String fromUrl;
	File toFile;

	public PictureLoader(Context context, String fromUrl, File toFile) {
		super(context);
		this.fromUrl = fromUrl;
		this.toFile = toFile;
	}

	@Override
	public PicrureLoaderResult loadInBackground() {
		try {
			byte[] buffer = new byte[1024];
			int fileLen = Integer.MAX_VALUE;
			URL url = new URL(fromUrl);
			URLConnection urlConnection = url.openConnection();
			if (urlConnection instanceof HttpURLConnection) {
				HttpURLConnection httpUrlConnection = (HttpURLConnection) urlConnection;
				httpUrlConnection.connect();

				int len = httpUrlConnection.getContentLength();
				if (len >= 0) {
					fileLen = len;
				}
				String type = httpUrlConnection.getContentType();
				if (type.indexOf("image/") != 0) {
					return new PicrureLoaderResult(LoaderResult.FAILED, Integer.valueOf(0));
				}
			} else {
				// lets assume that everything is ok
			}

			InputStream in = urlConnection.getInputStream();
			FileOutputStream fOut = new FileOutputStream(toFile);
			int len;
			int loaded = 0;
			deliverResult(new PicrureLoaderResult(LoaderResult.LOADING, 0));
			while ((len = in.read(buffer)) != -1) {
				fOut.write(buffer, 0, len);
				loaded += len;
				deliverResult(new PicrureLoaderResult(LoaderResult.LOADING, 100 * loaded / fileLen));
			}
			fOut.close();
			in.close();
			return new PicrureLoaderResult(LoaderResult.LOADED, 100);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return new PicrureLoaderResult(LoaderResult.FAILED, 0);
		} catch (IOException e1) {
			e1.printStackTrace();
			return new PicrureLoaderResult(LoaderResult.FAILED, 0);
		}
	}

	@Override
	protected void onStartLoading() {
	}
}