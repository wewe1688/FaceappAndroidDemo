package com.faceapp.demo.util;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * 基本工具类
 * 
 * @author Will
 * 
 */
public class ToolHelper {

	/**
	 * 检测网络连接是否正常
	 */
	public static boolean isConnectivity(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = manager.getActiveNetworkInfo();
		if (networkinfo != null && networkinfo.isAvailable()) {
			// networkinfo.getTypeName();
			return true;
		} else {
			return false;
		}
	}

	public static boolean isEmpty(String str) {
		return TextUtils.isEmpty(str);
	}

	public static void toast(Context mContext, String str) {
		Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
	}

	public static void Log(String TAG, String msg) {
		Log.i(TAG, msg);
	}
}
