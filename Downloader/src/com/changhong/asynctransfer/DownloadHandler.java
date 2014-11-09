package com.changhong.asynctransfer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.util.Log;

/**
 * 跟踪、报告下载进度及各个线程的状态，只供内部使用，提供给用户的接口是另一个抽象类，在本类的各个方法中有调用
 * @author Siwutian
 *
 */
public final class DownloadHandler{
	
	private AsyncDownloadHandler userHandler;
	
	public DownloadHandler(){
	}
	
	public DownloadHandler(AsyncDownloadHandler userHandler){
		this.userHandler = userHandler;
	}

	public void onDownloading(int[] loadedsSize) {
		int count = 0;
		for(int i:loadedsSize){
			count += i;
		}
		Log.e("Loading", String.valueOf(count));
		// 添加用户侦听
		if(userHandler != null){
			userHandler.onDownloading(count);
		}

	}

	public void onDownloadError(Exception e) {
		Log.e("Error", e.toString());
		if(userHandler != null){
			userHandler.onDownloadError(e);
		}
	}

	public void onDownloadCancel() {	
		Log.e("Cancel", "was cancel!");
		if(userHandler != null){
			userHandler.onDownloadCancel();
		}
	}

	public void onDownloadCompleted(Document doc) {
		Element root = doc.getDocumentElement();
		NodeList list = root.getChildNodes();
		if(list  == null || list.getLength() == 0){
			Log.e("Completed", "怎么可能，Document为空！");
		}else{
			for(int i = 0; i < list.getLength(); i++){
				Log.e("Completed", ((Element)list.item(i)).getAttribute("id") + "-" 
						+ ((Element)list.item(i)).getAttribute("startPosition") + "-"
						+ ((Element)list.item(i)).getAttribute("endPosition") + "-"
						+ ((Element)list.item(i)).getAttribute("downloadedSize") + "-"
						+ ((Element)list.item(i)).getAttribute("state"));	
			}
		}
		
		// 添加用户侦听,在最后一个线程完成的时候执行
		if(userHandler != null){
			userHandler.onDownloadCompleted("");
		}
	}

}
