package com.changhong.crazydog.location;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationData.Builder;
import com.baidu.mapapi.map.OverlayOptions;
import com.changhong.downloader.R;
import com.baidu.mapapi.model.LatLng;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ZoomControls;

public class LocationActivity extends Activity{
	
	private LocationClient client = null;
	private MapView mMapView = null; 
	private BaiduMap mBaiduMap;
	private BaiduMapContext context;
	private Boolean isFirstLoc = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());  
		setContentView(R.layout.activity_location);
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		initView();
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
   
    private void initView(){
    	 mMapView = (MapView) findViewById(R.id.bmapView); 
         mBaiduMap = mMapView.getMap();
         mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
         layers(mBaiduMap);
         cleanLogo(mMapView);
         addView(mMapView);
         client = new LocationClient(getApplicationContext()); 
 		 client.registerLocationListener(new LocationListener(this)); 
 		 setLocationOption(client);
 		 client.start();
    }
    
    private void setLocationOption(LocationClient client){
		 LocationClientOption option = new LocationClientOption();
		 option.setOpenGps(true);
		 option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02(国测局),bd09(百度墨卡托)
		 option.setScanSpan(2000);//设置发起定位请求的间隔时间为5000ms(GPS定位？)
		 option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
		 client.setLocOption(option);
    }
    
    private void cleanLogo(MapView mMapView){
    	 int count = mMapView.getChildCount();
         for (int i = 0; i < count; i++) {
             View child = mMapView.getChildAt(i);
             if (child instanceof ZoomControls || child instanceof ImageView) {
                 child.setVisibility(View.INVISIBLE);
             }
         }
    }
    
    public void layers(BaiduMap map){
    	map.setBaiduHeatMapEnabled(true);
    	map.setBuildingsEnabled(true);
    	map.setTrafficEnabled(true);
    }
    
    public void addView(MapView mapView){
    	Button btn = new Button(this);
    	btn.setWidth(100);
    	btn.setHeight(20);
    	btn.setVisibility(View.VISIBLE);
    	mapView.addView(btn);
    }
    private void addOverlay(float lat,float lgt){
    	//定义Maker坐标点  
		LatLng point = new LatLng(lat, lgt);  
		//构建Marker图标  
		BitmapDescriptor bitmap = BitmapDescriptorFactory  
		    .fromResource(R.drawable.ic_launcher);  
		//构建MarkerOption，用于在地图上添加Marker  
		OverlayOptions option = new MarkerOptions()  
		    .position(point)  
		    .icon(bitmap);  
		//在地图上添加Marker，并显示  
		mBaiduMap.addOverlay(option);
    }
    
    private class LocationListener implements BDLocationListener{
    	
    	private LocationActivity context;
    	public LocationListener(LocationActivity c){
    		this.context = c;
    	}
    	
    	@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null || mMapView == null)
				return;
			mBaiduMap.setMyLocationEnabled(true); 
			Builder builder = new MyLocationData.Builder();
			builder.accuracy(location.getRadius());
			builder.direction(location.getDirection());
			builder.latitude(location.getLatitude());
			builder.longitude(location.getLongitude());
			MyLocationData locData = builder.build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll,17);
				mBaiduMap.animateMapStatus(u);
			}

		}
    }
}
