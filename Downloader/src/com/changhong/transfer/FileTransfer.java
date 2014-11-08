package com.changhong.transfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import android.util.Log;

public class FileTransfer extends HttpRequestProvider{
	
	private ThreadExecutor executor = ThreadExecutor.defaultInstance();
	private String path;
	private String filename;

	public FileTransfer(String path,String filename){
		this.path = path;
		this.filename = filename;
	}

	public FileTransfer(){
		
	}

	public FileTransfer(String fullName){
		this.filename = fullName;
	}
	
	/**
	 * 启动线程进行下载，文件名和目录在构造函数里提供
	 * @param urlstr 文件的完整url包括文件名和后缀名
	 */
	public void asyncDownload(final String urlstr,final DownloadHandler downloadHandler){
		executor.doTask(new Runnable(){
			@Override
			public void run() {
				File file = setDownloadFile(path,filename);
				download(urlstr, file,downloadHandler);
			}
			
		});
	}
	
	/**
	 * 上传文件，文件路径（本地路径）在构造函数里提供。
	 * @param urlstr 服务地址：http://10.9.52.99:10000/upload
	 */
	public void asyncUpload(final String urlstr,final String fileName,final UploadHandler handler){
		executor.doTask(new Runnable(){
			@Override
			public void run() {
				File file = null;
				try{
					if(path == null){
						file = getUploadFile(filename);
					}else{
					    file = getUploadFile(path + filename);	
					}
					upload(urlstr,new FileInputStream(file),fileName,file.length(),handler);
				}catch(IOException e){
					Log.e("Upload_E",e.toString());
				}catch(Exception e){
					Log.e("Upload_E",e.toString());
				}
			}
			
		});
	}
	
	public void asyncStreamUpload(final String urlstr,final InputStream stream,
			final String fileName,final long fileSize,final UploadHandler handler){
		executor.doTask(new Runnable(){
			@Override
			public void run() {
				upload(urlstr, stream,fileName,fileSize,handler);
			}
		});
	}

	/**
	 * 文件列表
	 * @param urlstr 服务地址：http://10.9.52.99:10000/list
	 */
	public void asyncFielist(final String urlstr,final FileListHandler handler){
		executor.doTask(new Runnable(){
			@Override
			public void run() {
				getFilelist(urlstr,handler);
			}
			
		});
	}
	
	/**
	 * 获取要上传的文件名（带路径和后缀名）
	 * @param fullName
	 * @return
	 */
	private File getUploadFile(final String fullName){
		File file = new File(fullName);
		if(!file.exists() || !file.isFile()){
			throw new IllegalArgumentException("找不到文件:" + fullName);
		}
		return file;
		
	}
	
	/**
	 * 设置下载位置及文件名称
	 * @param filePath 文件夹
	 * @param filename 文件名
	 * @return
	 */
	private File setDownloadFile(String filePath,String filename){
		File path = null;
		try{
			path = new File(filePath);	
		}catch(Exception e){
			throw new IllegalArgumentException("非法的文件路径：" + filePath + filename);
		}
		if(!path.isDirectory() || !path.exists()){
			path.mkdirs();
		}
		File file = new File(path,filename);
		if(file.exists()){
			java.text.DateFormat format = new java.text.SimpleDateFormat("yyyyMMddhhmmss");
			file = new File(path,format.format(new Date()) + filename);
		}
		return file;
	}
	
	
//	public void asyncFileUpload(final String urlstr,final UploadHandler handler){
//		executor.doTask(new Runnable(){
//			@Override
//			public void run() {
//				File file = null;
//				try{
//					if(path == null){
//						file = getUploadFile(filename);
//					}else{
//					    file = getUploadFile(path + filename);	
//					}
//				}catch(Exception e){
//					Log.e("Upload_E",e.toString());
//				}
//				upload(urlstr, file,handler);
//			}
//			
//		});
//	}
}
