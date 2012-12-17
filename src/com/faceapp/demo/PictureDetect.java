package com.faceapp.demo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.faceapp.demo.http.FacePlusAPI;
import com.faceapp.demo.http.HttpManager;
import com.faceapp.demo.http.HttpParameters;
import com.faceapp.demo.http.HttpTask;
import com.faceapp.demo.http.HttpTask.NetRequestListener;
import com.faceapp.demo.object.Face_Detect;
import com.faceapp.demo.object.Return_Boolean;
import com.faceapp.demo.object.Return_Detection_Detect;
import com.faceapp.demo.util.FileHelper;
import com.faceapp.demo.util.TakePicture;
import com.faceapp.demo.util.ToolHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * A simple demo, get a picture form your phone<br />
 * Use the facepp api to detect<br />
 * Find all face on the picture, and mark them out.
 * @author moon5ckq
 */
public class PictureDetect extends BaseActivity implements OnClickListener {

	final private static String TAG = "PictureDetect";
	
	private String Person_ID ;
	private ImageView imageView = null;
	private Bitmap img = null;
	private Button buttonDetect = null;
	private TextView textView = null;
	private TakePicture mTakePicture ;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    protected void initView() {
        setContentView(R.layout.view_picturedetect);
        
        Person_ID = getIntent().getStringExtra(FacePlusAPI.PERSON_ID);
        if(!ToolHelper.isEmpty(Person_ID))
        	setTitle("图片信息将于用户ID:" + Person_ID);
        
        Button button = (Button)this.findViewById(R.id.button1);
        button.setOnClickListener(this);
        
        buttonDetect = (Button)this.findViewById(R.id.button2);
        buttonDetect.setVisibility(View.INVISIBLE);
        buttonDetect.setOnClickListener(this);
        
        textView = (TextView)this.findViewById(R.id.textView1);
        
        imageView = (ImageView)this.findViewById(R.id.imageView1);
        imageView.setImageBitmap(img);
    	
        mTakePicture = new TakePicture(this);
    	super.initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case TakePicture.CAMERA:
			File pictureZoom1 = new File(TakePicture.TEMP_PATH);
			mTakePicture.startPhotoZoom(Uri.fromFile(pictureZoom1)); 
			break;
		case TakePicture.CAMERAZOOM:
			File pictureZoom = new File(TakePicture.TEMP_PATH);
			mTakePicture.startPhotoZoom(Uri.fromFile(pictureZoom)); 
			break;
		case TakePicture.PHOTOZOOM:
			if(data!= null && data.getData() != null)
				mTakePicture.startPhotoZoom(data.getData());  
			break;
		case TakePicture.PHOTORESOULT:
			  if(data == null)
				  break;
	          Bundle extras = data.getExtras();   
	            if (extras != null) {  
	                img = extras.getParcelable("data");
	                ByteArrayOutputStream stream = new ByteArrayOutputStream();  
	                img.compress(Bitmap.CompressFormat.PNG, 75, stream);// (0 - 100)压缩文件  
	        		try {
	        			//多做一步删除操作 放置将拍照的源文件上传.
	        			FileHelper.deleteFile(TakePicture.TEMP_PATH);
	        			FileHelper.savePicture(FileHelper.getPicturePath() , TakePicture.TEMP_NAME, img);
	        			if(img != null){
	        				imageView.setImageBitmap(img);  
	        				buttonDetect.setVisibility(View.VISIBLE);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
	            }  
			break;
		}
    }
    
    @Override
    protected void onStop() {
    	if(img != null){
    		img.recycle();
    		img = null ;
    	}
    	super.onStop();
    }

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
		    mTakePicture.showPickPhotoDialog();
		    break;
		case R.id.button2:
	        new HttpTask(mContext, FacePlusAPI.DETECTION_DETECT, new HttpParameters().getFaceParameters(), HttpManager.HTTPMETHOD_POST,  TakePicture.TEMP_PATH, detectListener , "图片分析中...").execute("");
			break;
		default:
			break;
		}
	}
	
	private void addFaceFrame(final ArrayList<Face_Detect> faces){
		//use the red paint
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		paint.setStrokeWidth(Math.max(img.getWidth(), img.getHeight()) / 100f);

		//create a new canvas
		Bitmap bitmap = Bitmap.createBitmap(img.getWidth(), img.getHeight(), img.getConfig());
		Canvas canvas = new Canvas(bitmap);
		canvas.drawBitmap(img, new Matrix(), null);
		
		//find out all faces
		for (int i = 0; i < faces.size(); ++i) {
			float x, y, w, h;
			//get the center point
			x = faces.get(i).center.x ;
			y = faces.get(i).center.y ;
			//get face size
			w = faces.get(i).width ;
			h = faces.get(i).height ;		
			
			//change percent value to the real size
			x = x / 100 * img.getWidth();
			w = w / 100 * img.getWidth() * 0.7f;
			y = y / 100 * img.getHeight();
			h = h / 100 * img.getHeight() * 0.7f;

			//draw the box to mark it out
			canvas.drawLine(x - w, y - h, x - w, y + h, paint);
			canvas.drawLine(x - w, y - h, x + w, y - h, paint);
			canvas.drawLine(x + w, y + h, x - w, y + h, paint);
			canvas.drawLine(x + w, y + h, x + w, y - h, paint);
		}
		
		//save new image
		img = bitmap;

		PictureDetect.this.runOnUiThread(new Runnable() {
			public void run() {
				//show the image
				imageView.setImageBitmap(img);
				textView.setText("Finished, "+ faces.size() + " faces.");
			}
		});
		
	}
	
	/**
	 * 分析图片信息
	 */
	HttpTask.NetRequestListener detectListener = new NetRequestListener() {
		@Override
		public Object onRequest() {
			return null;
		}
		
		@Override
		public void onPreRequest() {
		}
		
		@Override
		public void onComplete(String response) {
			Return_Detection_Detect mResult ;
        	try {
        		mResult = new Gson().fromJson(response.trim(),new TypeToken<Return_Detection_Detect>() {}.getType());

        		if(mResult.face != null && mResult.face.size() >0){
        			addFaceFrame(mResult.face);
    				if (mResult.face == null || mResult.face.size() == 0 ) {
    					ToolHelper.toast(mContext, getString(R.string.hint_detect_noface));
    				} else {
    					// 将得到的Face信息,写入该用户中.
    					String faceid = "";
    					for (Face_Detect face : mResult.face) {
    						faceid = faceid + face.face_id + ",";
    					}
    					if (faceid.endsWith(","))
    						faceid = faceid.substring(0, faceid.length() - 1);
    					
    					if(!ToolHelper.isEmpty(Person_ID)){
    						HttpParameters mParameters = new HttpParameters().getFaceParameters();
    						mParameters.add(FacePlusAPI.PERSON_ID, Person_ID);
    						mParameters.add(FacePlusAPI.Face_ID, faceid);
    						new HttpTask(mContext, FacePlusAPI.Person_Add_Face, mParameters, HttpManager.HTTPMETHOD_GET, null, bindFaceListener, getString(R.string.hint_bind)).execute("");
    					}
    				}
        		}else{
        			textView.setText("Finished, "+ 0 + " faces.");
        		}
        	}catch (Exception e) {
				Log.i(TAG, e.toString());
				ToolHelper.toast(mContext, "返回数据错误");
			}
		}
		@Override
		public void onError(String response) {
		}
	};
	
	/**
	 * 将分析信息与Person绑定
	 */
	HttpTask.NetRequestListener bindFaceListener = new HttpTask.NetRequestListener() {
		@Override
		public Object onRequest() {
			return null;
		}

		@Override
		public void onPreRequest() {
		}

		@Override
		public void onComplete(String response) {
			Return_Boolean mResult;
			try {
				mResult = new Gson().fromJson(response.trim(), new TypeToken<Return_Boolean>() {
				}.getType());
				if (mResult.success) {
					ToolHelper.toast(mContext, getString(R.string.hint_bind_success));
					onRefresh();
				} else {
					ToolHelper.toast(mContext, getString(R.string.hint_bind_Fail));
				}
			} catch (Exception e) {
				ToolHelper.toast(mContext, getString(R.string.hint_bind_success));
			}
		}
		public void onError(String response) {};
	};
}
