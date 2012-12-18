package com.faceapp.demo;

import java.util.ArrayList;

import android.content.Intent;
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
 * 根据FaceID,返回Face信息.
 * @author Will
 */
public class Activity_Face extends BaseActivity implements OnItemClickListener  {
	
	private String Face_ID ;
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
    	Face_ID = getIntent().getStringExtra(FacePlusAPI.Face_ID);
        if(!ToolHelper.isEmpty(Face_ID)){
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
    	mParameters.add(FacePlusAPI.Face_ID, Face_ID);
    	new HttpTask(this, FacePlusAPI.Face_Get_Info, mParameters, HttpManager.HTTPMETHOD_GET,  null, getFaceListener, getString(R.string.hint_loading)).execute("");
    	super.onRefresh();
    }
    
    /**
     * 获取脸信息.和图片ID
     */
    HttpTask.NetRequestListener getFaceListener = new HttpTask.NetRequestListener() {
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
        		
        		if(mResult != null && mResult.face_info != null && mResult.face_info.size() > 0){
        			for (int i = 0; i < mResult.face_info.size(); i++) {
						mImageAdapter.add(mResult.face_info.get(i).img_id);
					}
        			mImageAdapter.notifyDataSetChanged();
        		}else{
        			ToolHelper.toast(mContext, getString(R.string.hint_detect_noface));
        		}
			} catch (Exception e) {
				ToolHelper.toast(mContext, getString(R.string.hint_data_error));
			}
		}
	};
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent mIntent = new Intent(mContext , Activity_Image.class);
		mIntent.putExtra(FacePlusAPI.Img_ID, mImageIDs.get(position));
		startActivity(mIntent);
	}

}