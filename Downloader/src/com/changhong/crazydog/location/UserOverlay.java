package com.changhong.crazydog.location;

import com.baidu.mapapi.model.LatLng;

public class UserOverlay {
	
	private String userId; // 用户的唯一标识
	private LatLng geoPoint; // 位置
	
	public UserOverlay(){
		
	}
	
	public UserOverlay(String id,LatLng geoPoint){
		this.userId = id;
		this.geoPoint = geoPoint;
	}
	
	public void setUserId(String id){
		this.userId = id;
	}
	
	public void setGeoPoint(LatLng geoPoint){
		this.geoPoint = geoPoint;
	}
	
	public String getUserId(){
		return this.userId;
	}
	
	public LatLng getGeoPoint(){
		return this.geoPoint;
	}
}
