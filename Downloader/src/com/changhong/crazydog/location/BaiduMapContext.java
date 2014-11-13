package com.changhong.crazydog.location;

import java.util.HashMap;
import com.baidu.mapapi.model.LatLng;

public class BaiduMapContext {

	private static BaiduMapContext baiduMapContext;  // 单一实例
	private LatLng currentLocation;                  // 用户最近一次定位的位置
	private HashMap<String,UserOverlay> usersOverlay; // 好友的位置列表
	
	private BaiduMapContext(){
	    usersOverlay = new HashMap<String,UserOverlay>();
	}
	
	public static BaiduMapContext getBaiduMapContext(){
		if(baiduMapContext == null){
			baiduMapContext = new BaiduMapContext();
		}
		return baiduMapContext;
	}
	
	public void addUserOverlay(UserOverlay user){
		this.usersOverlay.put(user.getUserId(), user);
	}
	
	public void deleteUserOverlay(String id){
		if(usersOverlay.containsKey(id)){
			this.usersOverlay.remove(id);
		}
	}
	
	public HashMap<String,UserOverlay> getUsersOverlay(){
		return this.usersOverlay;
	}
	
	public void setCurrentLocation(LatLng point){
		this.currentLocation = point;
	}
	
	public LatLng getCurrentLocation(){
		return currentLocation;
	}
	
}
