package com.changhong.transfer;

import java.util.ArrayList;
import java.util.List;

public abstract class FileListHandler {
	
	public abstract void onError(Exception e);
	
	public abstract void onLoading(long fileSize,int downloadSize);
	
	public abstract void onCancel(String response);
	
	public abstract void onCompleted(List<String> filelist);
	
	public final List<String> onCompleted(String listString){
		List<String> list = new ArrayList<String>();
		String[] strings = listString.split(";");
		for(String s:strings){
			list.add(s);
		}
		return list;
	}
}
