package com.changhong.downloader.activitys;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.nplatform.comapi.basestruct.GeoPoint;
import com.baidu.nplatform.comapi.map.MapController;
import com.changhong.downloader.R;
import com.baidu.mapapi.model.LatLng;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class LocationActivity extends Activity{
	
	private LocationClient client = null;
	private MapView mMapView = null; 
	private BaiduMap mBaiduMap;
	private Boolean isFirstLoc = true;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());  
		setContentView(R.layout.activity_location);
        mMapView = (MapView) findViewById(R.id.bmapView); 
        mBaiduMap = mMapView.getMap();
        client = new LocationClient(getApplicationContext()); // 声明LocationClient类
		client.registerLocationListener(new LocationListener()); // 注册监听函数
		client.start();
	}
	
	@Override  
	protected void onDestroy() {  
	    super.onDestroy();
	    mMapView.onDestroy();  
	}	
  
	@Override  
	protected void onResume() {  
	    super.onResume();
	    mMapView.onResume();  
	} 
	
    @Override  
	protected void onPause() {  
	    super.onPause();
	    mMapView.onPause();  
	}  
   
    private class LocationListener implements BDLocationListener{
    	@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
				mMapView.set
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
			
		}
    }
}
