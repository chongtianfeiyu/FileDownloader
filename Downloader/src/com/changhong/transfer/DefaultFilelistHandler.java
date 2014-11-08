package com.changhong.transfer;

import java.util.List;
import android.os.Message;
import android.util.Log;

public class DefaultFilelistHandler extends FileListHandler{

	private ViewHandler handler;
	
	public DefaultFilelistHandler(ViewHandler handler){
		this.handler = handler;
	}
	
	private void send(String msg){
		Message message = new Message();
		message.what = 1;
		handler.setMessage(msg + "\n");
		handler.sendMessage(message);
	}
	
	@Override
	public void onError(Exception e) {
		send(e.toString());
		Log.e("Filelist_Error",e.toString());
	}

	@Override
	public void onLoading(long fileSize, int downloadSize) {
		send(String.valueOf(fileSize) + String.valueOf(downloadSize));
		Log.e("Filelist_Loading",String.valueOf(fileSize) + String.valueOf(downloadSize));
	}

	@Override
	public void onCancel(String response) {
		send("已经取消下载文件列表！");
		Log.e("Filelist_Cancel","已取消下载文件列表");
	}

	@Override
	public void onCompleted(List<String> filelist) {
		// TODO Auto-generated method stub
		for(String s:filelist){
			Log.e("FileList_Completed", s);
			send("List_Completed:" + s);
		}
	}

}
