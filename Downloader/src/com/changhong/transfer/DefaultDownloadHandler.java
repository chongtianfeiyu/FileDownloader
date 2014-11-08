package com.changhong.transfer;

import java.io.File;
import android.os.Message;
import android.util.Log;

/**
 * 只为测试，下载状态信息根据实际需要处理
 * @author HuShenghai
 *
 */
public class DefaultDownloadHandler extends DownloadHandler{
	
	private ViewHandler handler;
	
	public DefaultDownloadHandler(ViewHandler handler){
		this.handler = handler;
	}
	
	private void send(String msg){
		Message message = new Message();
		message.what = 1;
		handler.setMessage(msg + "\n");
		handler.sendMessage(message);
	}

	@Override
	public void onLoading(long fileSize, int loadedSize) {
		send(String.valueOf(fileSize) + "  " + String.valueOf(loadedSize) 
				+ "  " + String.valueOf((int)((double)loadedSize/fileSize * 100)));
		Log.e("Downloading", "文件大小:" + String.valueOf(fileSize) + "  已下载大小:" + String.valueOf(loadedSize) 
				+ "  总进度:" + String.valueOf((double)loadedSize/fileSize * 100));
	}

	@Override
	public void onError(Exception e) {
		// 下载失败应该将文件删除
		send( e.toString() + "下载失败");
		Log.e("Downloading",e.toString());
	}

	@Override
	public void onCancel(File val) {
		// 取消后应该将已下载的部分文件删除
		send("你已经取消了下载，请删除文件");
		Log.e("Download","你已经取消了下载，请删除文件！");
	}

	@Override
	public void onCompleted(File in) {
		send(in.getPath() + "下载完成\n");
		Log.e("Downloaded",in.getPath() + "下载完成");
	}

}
