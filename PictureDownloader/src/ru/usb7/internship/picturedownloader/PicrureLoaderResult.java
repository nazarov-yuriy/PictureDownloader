package ru.usb7.internship.picturedownloader;

public class PicrureLoaderResult {
	public enum LoaderResult {
		IDLE, LOADING, FAILED, LOADED,
	}

	public LoaderResult loaderResult = LoaderResult.IDLE;
	public int loaderProgress = 0;
	public String errorMessage = "";

	public PicrureLoaderResult(LoaderResult result, int progress) {
		this.loaderResult = result;
		this.loaderProgress = progress;
	}
}
