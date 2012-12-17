package com.faceapp.demo;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.faceapp.demo.http.FacePlusAPI;
import com.faceapp.demo.http.HttpManager;
import com.faceapp.demo.http.HttpParameters;
import com.faceapp.demo.http.HttpTask;
import com.faceapp.demo.object.Return_FaceInfo;
import com.faceapp.demo.util.ToolHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Face列表
 * @author Will
 */
public class Activity_Image extends BaseActivity implements OnItemClickListener  {
	
	private String Img_ID ;
	private TextView resultView ;
	
	private ListView mListView;
	private ArrayList<String> mImageIDs;
	private ArrayAdapter<String> mImageAdapter;
	
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
        
        setContentView(R.layout.view_face);
        resultView = (TextView)findViewById(R.id.name);
        
        mListView = (ListView)findViewById(R.id.listview);
        mListView.setOnItemClickListener(this);
        mImageIDs = new ArrayList<String>();
        mImageAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, mImageIDs);
		mListView.setAdapter(mImageAdapter);

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
        	Return_FaceInfo mResult ;
        	try {
        		mResult = new Gson().fromJson(response.trim(),new TypeToken<Return_FaceInfo>() {}.getType());
			} catch (Exception e) {
				ToolHelper.toast(mContext, getString(R.string.hint_data_error));
			}
		}
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
	}

}