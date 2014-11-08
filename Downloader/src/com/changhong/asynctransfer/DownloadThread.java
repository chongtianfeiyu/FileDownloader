package com.changhong.asynctransfer;

import java.io.File;

public class DownloadThread extends RequestProvider implements Runnable{

	private File saveFile;
	private int threadId;
	private int startPosition;
	private int endPosition;
	private AsyncDownloadHandler handler;
	
	public DownloadThread(String url,File saveFile,int threadId,
			int startPosition,int endPosition,Object locker,AsyncDownloadHandler handler){
		super(url, locker);
		this.saveFile = saveFile;
		this.threadId = threadId;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.handler = handler;
	}
	
	@Override
	public void run() {
		get(threadId,saveFile,startPosition,endPosition,handler);
	}

}
