package com.changhong.asynctransfer;

import android.util.Log;

public class DownloadHandler extends AsyncDownloadHandler {

	@Override
	public void onDownloading(int[] loadedsSize) {
		int count = 0;
		for(int i:loadedsSize){
			count += i;
		}
		Log.e("Loading", String.valueOf(count));
	}

	@Override
	public void onDownloadError(Exception e) {
		Log.e("Error", e.toString());
	}

	@Override
	public void onDownloadCancel() {	
		Log.e("Cancel", "was cancel!");
	}

	@Override
	public void onDownloadCompleted(String threadInfo) {
		Log.e("Completed", threadInfo);
	}

}
