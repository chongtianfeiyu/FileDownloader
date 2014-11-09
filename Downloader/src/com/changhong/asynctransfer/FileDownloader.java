package com.changhong.asynctransfer;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.util.Log;

public class FileDownloader {

	private Document document;
	private List<DownloadThread> threads = new ArrayList<DownloadThread>();
	private int fileSize;
	private Object locker;
	private int threadCount;
	private ThreadExecutor excutor = ThreadExecutor.defaultInstance();
	
	public static int[] downloadedsSize;
	
	
	public FileDownloader(int threadCount){
		this.threadCount = threadCount;
		locker = new Object();
		downloadedsSize = new int[threadCount];
		for(int i = 0; i < threadCount; i++){
			downloadedsSize[i] = 0;
		}
		document = XMLHelper.createDocument();
	}
	
	private Future<String> getDownloadThreads(final String urlstr,final File file) throws IOException{
		Future<String> future = excutor.doTask(new Callable<String>(){
			@Override
			public String call() throws Exception {
				URL url = new URL(urlstr);
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.connect();
				fileSize = conn.getContentLength();
				conn.disconnect();
				if(fileSize < 0){
					throw new IOException("未知的文件大小");
				}
				int blockSize = fileSize / threadCount;
				int littleMore = fileSize % threadCount;// 有可能不能均分，多余部分放在最后一个线程
				int startPosition = 0;
				int endPosition = 0;
				for(int id = 0; id < threadCount; id++){
					startPosition = id * blockSize;
					if(id == threadCount - 1){
						endPosition = (id + 1) * blockSize + littleMore - 1;	
					}else{
						endPosition = (id + 1) * blockSize - 1;	
					}
					DownloadHandler handler = new DownloadHandler();
					DownloadThread thread = new DownloadThread(urlstr,file,document,id,
							startPosition,endPosition,locker,handler);
					threads.add(thread);
				}
				return String.valueOf(fileSize);
			}
			
		});
		return future;		
	}

	/**
	 * 启动下载
	 * @throws IOException 
	 */
	public void download(String urlstr,File file) throws IOException{
		// 创建根元素
		Element root = document.createElement("file");
		root.setAttribute("fileName", file.getName());
		root.setAttribute("url", urlstr);
		document.appendChild(root);
		
		Future<String> w = getDownloadThreads(urlstr,file);
		try {
			Log.e("GetFileSize:", w.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		for(int i = 0; i < threads.size(); i++){
			excutor.doTask(threads.get(i));
		}
	}
}
