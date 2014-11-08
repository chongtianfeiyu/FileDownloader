package com.changhong.asynctransfer;

public abstract class AsyncDownloadHandler {
	
	public abstract void onDownloading(int[] loadedsSize);
	
	public abstract void onDownloadError(Exception e);
	
	public abstract void onDownloadCancel();
	
	public abstract void onDownloadCompleted(String threadInfo);
	
}
