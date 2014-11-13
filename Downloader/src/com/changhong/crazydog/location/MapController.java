package com.changhong.crazydog.location;

import android.view.View;
import android.widget.ImageView;
import android.widget.ZoomControls;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;

/**
 * 1.控制图层显示（包括地图类型，交通图层，热力图等）
 * 2.控制覆盖物显示
 * 3.控制地图对象（比例尺、缩放按钮、指北针等等）的显示
 * 4.控制POI显示
 * @author HuShenghai
 *
 */
public class MapController {
	
	private  BaiduMap map;
	
	public MapController(BaiduMap map){
		this.map = map;
	}
	
	/**
	 * 设置地图类型（矢量地图还是栅格地图）
	 * @param mapType
	 */
	public void setMainLayer(MapType mapType){
		switch(mapType){
		case VECTOR:
			map.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			break;
		case IMAGE:
			map.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
			break;
		default:
			break;
		}
	}
	
	/**
	 * 设置是否显示交通图层
	 * @param visible
	 */
	public void setTrafficVisible(Boolean visible){
		map.setTrafficEnabled(visible);
	}
	
	/**
	 * 设置是否显示建筑图层
	 * @param visible
	 */
	public void setBuildingVisible(Boolean visible){
		map.setBuildingsEnabled(visible);
	}
	
	/**
	 * 设置是否显示热量图层
	 * @param visible
	 */
	public void setHeatMapVisible(Boolean visible){
		map.setBaiduHeatMapEnabled(visible);
	}
	
	/**
	 * 去除百度地图LOGO，再也也不用看到这么几个碍眼的字了。
	 * 务必在实例化百度地图之后马上调用，否则有可能影响到后面的显示
	 * @param mMapView
	 */
	public void cleanLogo(MapView mMapView){
    	 int count = mMapView.getChildCount();
         for (int i = 0; i < count; i++) {
             View child = mMapView.getChildAt(i);
             if (child instanceof ZoomControls || child instanceof ImageView) {
                 child.setVisibility(View.INVISIBLE);
             }
         }
	}
	
	
	
	
	
	public enum MapType{
		VECTOR,IMAGE
	}
}
