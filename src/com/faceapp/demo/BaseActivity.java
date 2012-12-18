package com.faceapp.demo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public class BaseActivity extends Activity{
	
	protected Context mContext ;
	protected final String TAG = this.getClass().getSimpleName(); 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mContext = this ;
		super.onCreate(savedInstanceState);
	}
	
	protected void initView(){
	}
	
	protected void setView(){
	}
	
	protected void onRefresh(){
	}
}
