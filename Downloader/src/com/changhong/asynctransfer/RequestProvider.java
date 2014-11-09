package com.changhong.asynctransfer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class RequestProvider {
	
	private String url;
	private Boolean cancel = false; // 取消标志，为true时取消下载或者上载
	private int timeOut = 3000;     // 链接超时，默认3秒
	private int bufferSize = 1024;  // 缓存大小，默认1024
	private Object locker;
	
	public RequestProvider(String url,Object lock){
		this.url = url;
		this.locker = lock;
	}
	
	public void setCancel(Boolean flag){
		cancel = flag;
	}
	
	public void setTimeOut(int time){
		this.timeOut = time;
	}
	
	public void setBufferSize(int size){
		this.bufferSize = size;
	}
	
	private HttpURLConnection httpGetConnection(String urlstr){
		URL url;
		HttpURLConnection conn = null;
		try {
			url = new URL(urlstr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(timeOut);
			conn.setRequestMethod("GET");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return conn;
	}
		
	private void closeStream(Closeable stream){
		if(stream != null){
			try{
				stream.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	private String getErrorMessage(HttpURLConnection conn){
		BufferedReader breader = null;
		StringBuilder sb = new StringBuilder();
		String line = "";
		String code = "";
		try{
			code = String.valueOf(conn.getResponseCode());
			breader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while((line = breader.readLine()) != null){
				sb.append(line);
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			closeStream(breader);
			conn.disconnect();
		}
		return code + ":" + breader.toString();
	}
	
	private void setResult(Document doc,int threadId,int start,int end,int loaded,String state){
		Element root = doc.getDocumentElement();
		Element e = doc.createElement("part");
		e.setAttribute("id", String.valueOf(threadId));
		e.setAttribute("startPosition", String.valueOf(start));
		e.setAttribute("endPosition", String.valueOf(end));
		e.setAttribute("downloadedSize", String.valueOf(loaded));
		e.setAttribute("state", state);
		root.appendChild(e);
	}
	
	protected void get(int threadId,File file,int startPosition,int endPosition,Document doc,
			DownloadHandler handler){
		if(handler == null){
			return ;
		}
		HttpURLConnection conn = httpGetConnection(url);
		if(conn == null){
			handler.onDownloadError(new NullPointerException("无效的连接！"));
		}
		RandomAccessFile accessFile = null;
		BufferedInputStream inputStream = null;
        int downloadedSize = 0;
        String state = "";
		try{
			conn.setRequestProperty("Range", "bytes="+startPosition+"-"+endPosition);
			conn.connect();
			int code = conn.getResponseCode();
			if(code == 206){
		        byte[] buffer = new byte[bufferSize];
		        byte[] download = new byte[endPosition - startPosition + 1]; 
		        inputStream = new BufferedInputStream(conn.getInputStream());
		        int readLength = 0;
		        while((readLength = inputStream.read(buffer)) != -1 && !cancel){
		        	System.arraycopy(buffer, 0, download, downloadedSize, readLength);
		        	downloadedSize += readLength;
		        	FileDownloader.downloadedsSize[threadId] = downloadedSize;
		        	handler.onDownloading(FileDownloader.downloadedsSize);
		        }if(cancel){
		        	handler.onDownloadCancel();
		        }else{
		        	synchronized(locker){
						accessFile = new RandomAccessFile(file,"rwd");
						accessFile.seek(startPosition);
			        	accessFile.write(download,0,downloadedSize);
			        	accessFile.close();
			        	state = "Completed";
			        	setResult(doc,threadId,startPosition,endPosition,downloadedSize,state);
		        	}
		        }
			}else{
				handler.onDownloadError(new IOException(getErrorMessage(conn)));
				state = "ERROR";
				setResult(doc,threadId,startPosition,endPosition,downloadedSize,state);
				handler.onDownloadCompleted(doc);
			}
		} catch(IOException e){
			handler.onDownloadError(e);
			state = "ERROR";
			setResult(doc,threadId,startPosition,endPosition,downloadedSize,state);
		} finally{
			closeStream(inputStream);
			closeStream(accessFile);
			conn.disconnect();
        	handler.onDownloadCompleted(doc);
		}
	}
	
}
