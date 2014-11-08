package com.changhong.transfer;

import java.io.File;

public abstract class DownloadHandler {
	
	/**
	 * 下载完成事件
	 * @param in 
	 */
	public abstract void onCompleted(File in);
	
	/**
	 * 下载出现异常
	 * @param e
	 */
	public abstract void onError(Exception e);
	
	/**
	 * 下载过程中执行，如，返回下载百分比
	 * @param per
	 */
	public abstract void onLoading(long fileSize,int downloadSize);
	
	/**
	 * 下载取消时该将已下载的部分文件删除
	 */
	public abstract void onCancel(File file);
	
}
