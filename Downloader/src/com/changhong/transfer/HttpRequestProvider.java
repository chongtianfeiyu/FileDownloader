package com.changhong.transfer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpRequestProvider {
	
	private Boolean cancel = false; // 取消标志，为true时取消下载或者上载
	private int timeOut = 3000;     // 链接超时，默认3秒
	private int bufferSize = 1024;  // 缓存大小，默认1024
	
	public HttpRequestProvider(){
		// 保留
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
	
	/**
	 * 下载文件
	 * @param urlstr 文件连接，完整的url
	 * @param file 新建本地文件，用于写入要下载的文件
	 * @param downloadHandler
	 */
	protected void download(String urlstr,File file,DownloadHandler downloadHandler){
		if(downloadHandler == null){
			throw new NullPointerException("DownloadHandler不能为空!");
		}
		if(file == null || file.isDirectory()){
			downloadHandler.onError(new NullPointerException("无效的文件，无法创建文件。"));
			return;
		}
		HttpURLConnection conn = httpGetConnection(urlstr);
		if(conn == null){
			downloadHandler.onError(new NullPointerException("无效的url，无法创建HttpURLConnection."));
			return;
		}
		RandomAccessFile accessFile = null;
		BufferedInputStream bs = null;
		try {
			conn.connect();
			if(conn.getResponseCode() == 200){
				accessFile = new RandomAccessFile(file,"rwd");
				accessFile.seek(0);
		        byte[] buffer = new byte[bufferSize];
		        bs = new BufferedInputStream(conn.getInputStream());
		        int len = 0;
		        int downloadSize = 0;
		        while((len = bs.read(buffer,0,buffer.length))!=-1 && !cancel){
		        	accessFile.write(buffer,0,len);
		        	downloadSize += len;
		        	downloadHandler.onLoading(conn.getContentLength(), downloadSize);
		        }
		        if(cancel){
		        	downloadHandler.onCancel(file);
		        }else{
		        	downloadHandler.onCompleted(file);
		        }
			}else{   // 链接有错误，但是服务器有返回
				downloadHandler.onError(new Exception(getErrorMessage(conn))); 
			}
		} catch (IOException e) {
			downloadHandler.onError(e);
		} finally{
			closeStream(bs);
			closeStream(accessFile);
			conn.disconnect();
		}
	}

	/**
	 * 上传文件
	 * @param urlstr 服务地址
	 * @param inputStream 文件流
	 * @param filename 文件名（保存到服务器的）
	 * @param fileSize 文件大小，用于监测上传进度
	 * @param uploadHandler
	 */
	protected void upload(String urlstr,InputStream inputStream,String filename,long fileSize,UploadHandler uploadHandler){
		if(uploadHandler == null){
			throw new IllegalArgumentException("或许你想使用上传功能，请使用带UploadHandler的构造函数。");
		}
		if(inputStream == null){
			uploadHandler.onError(new NullPointerException("无效的文件流"));
			return;
		}
		HttpURLConnection conn = httpPostConnection(urlstr);
		if(conn == null){
			uploadHandler.onError(new NullPointerException("无效的url，无法创建HttpURLConnection."));
			return;
		}
		OutputStream outputStream = null;
		InputStream in = null;
		try {
			conn.setRequestProperty("File_Name",filename);
			conn.connect();
			outputStream = conn.getOutputStream();
			byte[] buffer = new byte[bufferSize];
			int length = -1;
			int uploadedSize = 0;
			while(((length = inputStream.read(buffer)) != -1) && !cancel){
				outputStream.write(buffer,0,length);
				uploadedSize += length;
				uploadHandler.onLoading(fileSize, uploadedSize);
			}
			if(conn.getResponseCode() == 200){
				StringBuffer sbuffer = new StringBuffer();
				in = conn.getInputStream();
				while((length = in.read(buffer)) != -1){
					sbuffer.append(new String(buffer,0,length,"UTF-8"));
				}
				if(!cancel){
					uploadHandler.onCompleted(sbuffer.toString());
				}else{
					uploadHandler.onCancel(sbuffer.toString());
				}
			}else{
				uploadHandler.onError(new Exception(getErrorMessage(conn)));
			}
		} catch (IOException e) {
			uploadHandler.onError(e);
		} finally{
			closeStream(in);
			closeStream(inputStream);
			conn.disconnect();
		}
	}

	/**
	 * 获取文件列表
	 * @param urlstr 服务地址
	 * @param filelistHandler
	 */
	protected void getFilelist(String urlstr,FileListHandler filelistHandler){
		if(filelistHandler == null){
			throw new IllegalArgumentException("或许你想使用文件列表功能，请使用带FileListHandler的构造函数。");
		}
		HttpURLConnection conn = httpGetConnection(urlstr);
		if(conn == null){
			filelistHandler.onError(new NullPointerException("无效的url，无法创建HttpURLConnection."));
			return;
		}
		InputStream in = null;
		try {
			conn.connect();
			if(conn.getResponseCode() == 200){
				in = conn.getInputStream();
				StringBuffer sBuffer = new StringBuffer();
				byte[] buffer = new byte[bufferSize];
				int length = -1;
				int downloadedSize = 0;
				while(((length = in.read(buffer)) != -1) && !cancel){
					sBuffer.append(new String(buffer, 0, length, "utf-8"));
					downloadedSize += length;
					filelistHandler.onLoading(conn.getContentLength(), downloadedSize);
				}
				if(!cancel){
					filelistHandler.onCompleted(filelistHandler.onCompleted(sBuffer.toString()));
				}else{
					filelistHandler.onCancel("任务被终止！");
				}
			}else{    // 链接失败但是服务器有返回（错误流ErrorStream）
				filelistHandler.onError(new Exception(getErrorMessage(conn))); 
			}
		} catch(IOException e){
			filelistHandler.onError(e);
		} finally{
			closeStream(in);
			conn.disconnect();
		}
	}
	
	private String getErrorMessage(HttpURLConnection conn){
		// 连接失败但服务器仍然发送了有用数据，则返回错误流.
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
	
	private HttpURLConnection httpPostConnection(String urlstr){
		URL url;
		HttpURLConnection conn = null;
		try {
			url = new URL(urlstr);
			conn = (HttpURLConnection)url.openConnection();
			conn.setConnectTimeout(timeOut);
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestProperty("Charset", "UTF-8");
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
//	
//	protected void upload(String urlstr,File file,UploadHandler uploadHandler){
//		if(uploadHandler == null){
//			throw new IllegalArgumentException("或许你想使用上传功能，请使用带UploadHandler的构造函数。");
//		}
//		if(file == null){
//			uploadHandler.onError(new NullPointerException("无效的文件路径，无法创建文件."));
//			return;
//		}
//		HttpURLConnection conn = httpPostConnection(urlstr);
//		if(conn == null){
//			uploadHandler.onError(new NullPointerException("无效的url，无法创建HttpURLConnection."));
//			return;
//		}
//		try {
//			conn.setRequestProperty("File_Name",file.getName());
//			OutputStream outputStream = conn.getOutputStream();
//			FileInputStream fileInput = new FileInputStream(file);
//			byte[] buffer = new byte[bufferSize];
//			int length = -1;
//			int uploadedSize = 0;
//			while(((length = fileInput.read(buffer)) != -1) && !cancel){
//				outputStream.write(buffer,0,length);
//				uploadedSize += length;
//				uploadHandler.onLoading(file.length(), uploadedSize);
//			}
//			if(conn.getResponseCode() == 200){
//				StringBuffer sbuffer = new StringBuffer();
//				InputStream in = conn.getInputStream();
//				while((length = in.read(buffer)) != -1){
//					sbuffer.append(new String(buffer,0,length,"UTF-8"));
//				}
//				in.close();
//				fileInput.close();
//				outputStream.close();
//				conn.disconnect();
//				if(!cancel){
//					uploadHandler.onCompleted(sbuffer.toString());
//				}else{
//					uploadHandler.onCancel(sbuffer.toString());
//				}
//			}else{
//				uploadHandler.onError(new Exception(getErrorMessage(conn)));
//			}
//		} catch (MalformedURLException e) {
//			uploadHandler.onError(e);
//		} catch (IOException e) {
//			uploadHandler.onError(e);
//		}
//	}
}
