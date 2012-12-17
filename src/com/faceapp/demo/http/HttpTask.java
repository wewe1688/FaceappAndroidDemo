package com.faceapp.demo.http;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.faceapp.demo.R;
import com.faceapp.demo.util.ToolHelper;

public class HttpTask extends AsyncTask<String, Void, String> {

	private Context mContext ;
	private AsyncTask<String, Void, String> mAsyncTask ;
	
	private String url ;
	private HttpParameters parameters ;
	private String methed ;
	private String filePath ; 
	
	private NetRequestListener listener ; 
	private String loadingStr ;
	private Dialog loadingDialog ;
	
	private final String TAG = this.getClass().getSimpleName();
	
	/**
	 * 上传文件
	 * @param context
	 * @param url
	 * @param params
	 * @param files
	 * @param listener
	 * @param loadingStr
	 */
	public HttpTask(Context context ,String url , HttpParameters _Parameters,String methed , String filePath , NetRequestListener listener , String loadingStr) {
		this.mContext = context;
		this.url = url;
		this.parameters = _Parameters;
		this.methed = methed ;
		this.filePath = filePath ;
		
		this.listener = listener;
		this.loadingStr = loadingStr;
		
		mAsyncTask = this ;
	}
	
	@Override
	protected void onPreExecute() {
		Log.i(TAG, "onPreExecute-");
		if(!ToolHelper.isConnectivity(mContext)){
			ToolHelper.toast(mContext, "没有网络");
			cancel(true);
		}
		
		if(!ToolHelper.isEmpty(loadingStr))//当有提示语句,显示对话框
			loadingDialog = loadingDialog(mContext, loadingStr);
		else{//无提示,在Title显示
			listener.onPreRequest();
		}
		super.onPreExecute();
	}
	
	@Override
	protected String doInBackground(String... params) {
		try {
			return  HttpManager.openUrl(url, methed, parameters, filePath);
		} catch (Exception e) {
			return e.toString();
		}
	}
	
	@Override
	protected void onPostExecute(String result) {
		if(loadingDialog != null){
			loadingDialog.dismiss();
			loadingDialog = null ;
		}
		//可根据实际情况设置
		if(ToolHelper.isEmpty(result)){
			ToolHelper.toast(mContext, "服务器返回为空.");
		}else if(result.contains("error")){
			listener.onError(result);
		}else{
			listener.onComplete(result);
			/**
//			try {
//				//JustTemplate<String> object = (JustTemplate<String>) new Gson().fromJson(result,new TypeToken<JustTemplate<String>>() {}.getType());
//				//if(!ResponseHelper.InspectCode(context, object.getCode(), object.getMsg()))
//					return ;
//			} catch (Exception e) {
//			}
			 * */
		}
		
		super.onPostExecute(result);
	}
	
	@Override
	protected void onCancelled() {
		if(loadingDialog != null){
			loadingDialog.dismiss();
			loadingDialog = null ;
		}
		ToolHelper.toast(mContext, "请求被取消.");
		super.onCancelled();
	}
	
    public static interface NetRequestListener {
    	/** 准备工作. **/
    	public void onPreRequest() ;
    	
    	/** 
    	 * 加载处理. 
    	 * 有些数据需要特殊处理,可以调用此方法.
    	 */
    	public Object onRequest();
    	
    	/** 当完成时.数据处理工作. **/
        public void onComplete(String response);
        /** **/
        public void onError(String response);

    }
    
	/**
	 * 等待框
	 * @return
	 */
	public Dialog loadingDialog(Context mContext , String str ){
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
		
		View mView = LayoutInflater.from(mContext).inflate(R.layout.compoent_dialog, null);
		
		ImageView mProgressImage = (ImageView)mView.findViewById(R.id.ProgressImage);
		final AnimationDrawable animationDrawable = (AnimationDrawable) mProgressImage.getDrawable();  

		ImageView mProgressClose = (ImageView)mView.findViewById(R.id.ProgressClose);
		mProgressClose.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mAsyncTask.cancel(true);
			}
		});

		TextView textView = (TextView)mView.findViewById(R.id.ProgressContent);
		if(!ToolHelper.isEmpty(str)){
			textView.setText(str);
		}
		mBuilder.setView(mView);

		AlertDialog mDialog = mBuilder.create(); 
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setCancelable(false);
		
		mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			public void onShow(DialogInterface dialog) {
				animationDrawable.start();
			}
		});
		
		mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				animationDrawable.stop();
			}
		});
		
		mDialog.show();
		/**
		ProgressDialog dialog = new ProgressDialog(mContext);
		dialog.setMessage(str);
		
		dialog.setIndeterminate(true);
		//禁止点击对话框意外,关闭
		dialog.setCanceledOnTouchOutside(false);
		//禁止按退后键关闭.
		dialog.setCancelable(false);
		
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
			}
		});
		
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
			}
		});
		*/
		
		return mDialog;
	}

}
