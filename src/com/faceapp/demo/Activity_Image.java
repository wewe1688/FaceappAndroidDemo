package com.faceapp.demo;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.faceapp.demo.http.FacePlusAPI;
import com.faceapp.demo.http.HttpManager;
import com.faceapp.demo.http.HttpParameters;
import com.faceapp.demo.http.HttpTask;
import com.faceapp.demo.object.Image;
import com.faceapp.demo.util.ToolHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 获取一张image的信息, 包括其中包含的face等信息
 * @author Will
 */
public class Activity_Image extends BaseActivity   {
	
	private String Img_ID ;
	private TextView resultView ;
	private ImageView mImageView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }
    
    @Override
    protected void initView() {
    	Img_ID = getIntent().getStringExtra(FacePlusAPI.Img_ID);
        if(!ToolHelper.isEmpty(Img_ID)){
        	onRefresh();
        }
        setContentView(R.layout.view_image);
        resultView = (TextView)findViewById(R.id.name);
        
        mImageView = (ImageView)findViewById(R.id.image);
        
    	super.initView();
    }
    
    @Override
    protected void onRefresh() {
    	HttpParameters mParameters = new HttpParameters().getFaceParameters();
    	mParameters.add(FacePlusAPI.Img_ID, Img_ID);
    	new HttpTask(this, FacePlusAPI.Face_Get_Img, mParameters, HttpManager.HTTPMETHOD_GET,  null, getImageListener, getString(R.string.hint_loading)).execute("");
    	super.onRefresh();
    }
	
    /**
     * 根据图片ID获取图片信息.
     */
    HttpTask.NetRequestListener getImageListener = new HttpTask.NetRequestListener() {
		@Override
		public Object onRequest() {
			return null;
		}
		@Override
		public void onPreRequest() {
		}
		public void onError(String response) {};
		@Override
		public void onComplete(String response) {
        	resultView.setText(response);
        	Image mResult ;
        	try {
        		mResult = new Gson().fromJson(response.trim(),new TypeToken<Image>() {}.getType());
			} catch (Exception e) {
				Log.i(TAG, e.toString());
				ToolHelper.toast(mContext, getString(R.string.hint_data_error) + e.toString());
			}
		}
	};

}