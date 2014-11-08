package com.changhong.transfer;

import android.os.Handler;
import android.os.Message;
import android.widget.EditText;

public class ViewHandler extends Handler{
	private String message;
	private EditText file_log_txt;
	
	public ViewHandler(EditText text){
		this.file_log_txt = text;
	}
	public void setMessage(String message){
		this.message = message;
	}
	  @Override
	  public void handleMessage(Message msg) {
		  super.handleMessage(msg);
		  if(msg.what == 1){
			  file_log_txt.getText().append(message);
		  }
	  }
}
