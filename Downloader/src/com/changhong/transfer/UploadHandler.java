package com.changhong.transfer;

public abstract class UploadHandler {
	
	public abstract void onCompleted(String complete);
	
	public abstract void onError(Exception e);
	
	public abstract void onCancel(String response);
	
	public abstract void onLoading(long fileSize,int uploadedSize);
}
