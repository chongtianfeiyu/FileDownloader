package com.changhong.asynctransfer;

import java.io.File;

import org.w3c.dom.Document;

public class DownloadThread extends RequestProvider implements Runnable{

	private File saveFile;
	private int threadId;
	private int startPosition;
	private int endPosition;
	private DownloadHandler handler;
	private Document document;
	
	public DownloadThread(String url,File saveFile,Document document,int threadId,
			int startPosition,int endPosition,Object locker,DownloadHandler handler){
		super(url, locker);
		this.saveFile = saveFile;
		this.threadId = threadId;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.document = document;
		this.handler = handler;
	}
	
	@Override
	public void run() {
		get(threadId,saveFile,startPosition,endPosition,document,handler);
	}

}
