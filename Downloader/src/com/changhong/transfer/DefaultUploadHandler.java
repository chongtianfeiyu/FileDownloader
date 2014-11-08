package com.changhong.transfer;

import android.os.Message;
import android.util.Log;

public class DefaultUploadHandler extends UploadHandler {

	private ViewHandler handler;
	
	public DefaultUploadHandler(ViewHandler vh){
		this.handler = vh;
	}
	
	private void send(String msg){
		Message message = new Message();
		message.what = 1;
		handler.setMessage(msg + "\n");
		handler.sendMessage(message);
	}
	
	@Override
	public void onCompleted(String complete) {
		send("upload completed!" + complete);
		Log.e("Upload_Completed", "Completed" + complete);
	}

	@Override
	public void onError(Exception e) {
		send("upload error:" + e.toString());
		Log.e("Upload_Error", e.toString());
	}

	@Override
	public void onCancel(String response) {
		Log.e("Upload_Cancel", "cancel");
	}

	@Override
	public void onLoading(long fileSize, int uploadedSize) {
		send(String.valueOf(fileSize) + "  " + String.valueOf(uploadedSize));
		Log.e("Uploading", String.valueOf(fileSize) + "  " + String.valueOf(uploadedSize));
	}

}
