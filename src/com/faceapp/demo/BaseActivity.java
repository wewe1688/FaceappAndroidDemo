package com.faceapp.demo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.faceapp.demo.http.HttpTask;
import com.faceapp.demo.object.Return_Boolean;
import com.faceapp.demo.util.ToolHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
	
	HttpTask.NetRequestListener deleteListener = new HttpTask.NetRequestListener() {
		@Override
		public Object onRequest() {
			return null;
		}

		@Override
		public void onPreRequest() {
		}

		@Override
		public void onComplete(String response) {
			Return_Boolean result;
			try {
				result = new Gson().fromJson(response.trim(),new TypeToken<Return_Boolean>() {}.getType());
				if (result != null && result.success ) {
					ToolHelper.toast(mContext, getString(R.string.hint_action_success));
					onRefresh();
				} else {
					ToolHelper.toast(mContext, getString(R.string.hint_action_fail));
				}
			} catch (Exception e) {
				ToolHelper.toast(mContext, getString(R.string.hint_action_fail));
			}
		}
		public void onError(String response) {};
	};


}
