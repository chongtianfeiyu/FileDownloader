package com.changhong.downloader.activitys;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.changhong.asynctransfer.AsyncDownloadHandler;
import com.changhong.asynctransfer.DownloadHandler;
import com.changhong.asynctransfer.FileDownloader;
import com.changhong.downloader.R;
import com.changhong.transfer.DefaultDownloadHandler;
import com.changhong.transfer.DefaultFilelistHandler;
import com.changhong.transfer.DefaultUploadHandler;
import com.changhong.transfer.FileTransfer;
import com.changhong.transfer.FileListHandler;
import com.changhong.transfer.UploadHandler;
import com.changhong.transfer.ViewHandler;

public class DownloadTestActivity extends Activity implements OnClickListener{
	
	private Button download_btn;
	private Button upload_btn;
	private Button filelist_btn;
	private EditText file_log_txt;
	private ViewHandler viewHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_downloadtest);
		initView();
	}
	private void initView() {
		download_btn = (Button)findViewById(R.id.activity_downloadtest_action_download);
		download_btn.setOnClickListener(this);
		upload_btn = (Button)findViewById(R.id.activity_downloadtest_action_upload);
		upload_btn.setOnClickListener(this);
		filelist_btn = (Button)findViewById(R.id.activity_downloadtest_action_filelist);
		filelist_btn.setOnClickListener(this);
		file_log_txt = (EditText)findViewById(R.id.activity_downloadtest_input_file_log);
		viewHandler = new ViewHandler(file_log_txt);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.activity_downloadtest_action_download:
			down();
			break;
		case R.id.activity_downloadtest_action_upload:
			upload();
			break;
		case R.id.activity_downloadtest_action_filelist:
			filelist();
			break;
		default:break;
		}
	}
	
	private void filelist() {
		String url = "http://10.9.52.99:10000/list";
		FileListHandler handler = new DefaultFilelistHandler(viewHandler);
		FileTransfer loader = new FileTransfer();
		loader.asyncFielist(url,handler);
	}
	private void upload() {
		String path = Environment.getExternalStorageDirectory().getPath()+"/Changhong/Download/";
		String filename = "tutu3.jpg"; // 文件名
		String url = "http://10.9.52.99:10000/upload";
		UploadHandler handler = new DefaultUploadHandler(viewHandler);
		FileTransfer loader = new FileTransfer(path+filename);
		loader.asyncUpload(url,"stream_test.jpg",handler);
	}
	
	private void down() {
//		// 下载位置，不存在则自动创建
//		String path = Environment.getExternalStorageDirectory().getPath()+"/Changhong/Download/";
//		String filename = "tutu3.jpg"; // 文件名
//		String urlstr = "http://g.hiphotos.baidu.com/image/pic/item/b3fb43166d224f4a4e5e8f080bf790529922d1e2.jpg"; // 文件的完整URL路径，后面需要用自己的服务地址代替
//		DownloadHandler handler = new DefaultDownloadHandler(viewHandler); 
//		FileTransfer loader = new FileTransfer(path,filename);
//		loader.asyncDownload(urlstr,handler);
//		loader.cancelDownload(); // 取消下载
		///////////////////////// 多线程下载测试 2014.11.08//////////////////////////
		String path = Environment.getExternalStorageDirectory().getPath()+"/Changhong/Download/";
		String filename = "ccc.gif"; // 文件名
		String urlstr = "http://c.hiphotos.baidu.com/image/pic/item/dcc451da81cb39dbf49ac758d3160924ab183061.jpg";
		File file = setDownloadFile(path,filename);
		FileDownloader downloader = new FileDownloader(5);
		try {
			downloader.download(urlstr,file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
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
}
