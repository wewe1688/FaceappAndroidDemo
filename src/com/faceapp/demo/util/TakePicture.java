   package com.faceapp.demo.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * 选取图片工具类
 * @author Will
 *
 */
public class TakePicture {
	
    public static final int PHOTO = 5;
    public static final int PHOTOZOOM = 6;
    
    public static final int CAMERA = 7;
    public static final int CAMERAZOOM = 8;
    
    public static final int PHOTORESOULT = 9 ;
    
    public static final String[] Actions = {"拍照上传" , "本地上传"};
    
    public static final String IMAGE_UNSPECIFIED = "image/*";
    public static final String TEMP_NAME = "temp.jpg";
    public static final String TEMP_PATH = FileHelper.getPicturePath() + TEMP_NAME;
    public static final int SIZE = 300 ;

    public Activity activity ;
    
    public TakePicture(Activity _activity) {
    	this.activity = _activity;
    }
    
	 /**
	   * 生成时间图片名
	   */
	  public static String getPhotoFileName() {
	      Date date = new Date(System.currentTimeMillis());
	      SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
	      return dateFormat.format(date) + ".png";
	  }
	  
	    /**
	     * 通过图库获取图片,并进行剪裁.
	     * @param context
	     */
	    public void doPhoto(){
	    	Intent intent = new Intent(Intent.ACTION_PICK, null);  
           intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);  
           activity.startActivityForResult(intent, PHOTO);  
	    }
	  
	    /**
	     * 通过图库获取图片,并进行剪裁.
	     * @param context
	     */
	    public void doPhoto_Zoom(){
	    	 Intent intent = new Intent(Intent.ACTION_PICK, null);  
             intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);  
             activity.startActivityForResult(intent, PHOTOZOOM);  
	    }
	    
	    /**
	     * 直接获取由相机返回的图片
	     * @param context
	     */
	    public void doCamera(){
	    	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
	    	intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(TEMP_PATH)));  
	    	activity.startActivityForResult(intent, CAMERA);  
	    }
	    
	    /**
	     * 直接获取由相机返回的图片
	     * @param context
	     */
	    public void doCamera_Zoom(){
	    	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
	    	intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(TEMP_PATH)));  
	    	activity.startActivityForResult(intent, CAMERAZOOM);  
	    }
	    
	    /**
	     * 对图片进行缩放
	     * @param activity
	     * @param uri
	     */
	    public void startPhotoZoom(Uri uri) {  
	        Intent intent = new Intent("com.android.camera.action.CROP");  
	        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);  
	        intent.putExtra("crop", "true"); 
	        // aspectX aspectY 是宽高的比例  
	        intent.putExtra("aspectX", 1);  
	        intent.putExtra("aspectY", 1);  
	        // outputX outputY 是裁剪图片宽高  
	        intent.putExtra("outputX", SIZE);  
	        intent.putExtra("outputY", SIZE);  
	        intent.putExtra("return-data", true);  
	        activity.startActivityForResult(intent, PHOTORESOULT);  
	    } 
	
		/**
		 * 获取当前Uri的真实文件路径
		 * @param context
		 * @param uri
		 * @return
		 */
		public static String getPathByUri(Activity context , Uri uri){
			String[] proj = { MediaStore.Images.Media.DATA };
//			context.managedQuery(uri , proj , null , null , null);
			Cursor actualimagecursor = context.getContentResolver().query(uri , proj , null , null , null);
			if(actualimagecursor != null && actualimagecursor.getCount() > 0){
				int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				actualimagecursor.moveToFirst();
				return actualimagecursor.getString(actual_image_column_index);
			}
			return null ;
		}
	
		/**
		 * 获取照片选择对话框
		 */
		public void showPickPhotoDialog() {
			// Wrap our context to inflate list items using correct theme
			//final Context dialogContext = new ContextThemeWrapper(activity , android.R.style.Theme_Light);
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setTitle("选择上传图片方式");
			builder.setItems(Actions, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					switch (which) {
					case 0:
						doCamera_Zoom();
						break;
					case 1:
						doPhoto_Zoom();
						break;
					}
				}
			});
			builder.create().show();
		}
		
		/**
		 * 	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TakePicture.CAMERA:
			File pictureZoom1 = new File(TakePicture.TEMP_PATH);
			mTakePicture.startPhotoZoom(Uri.fromFile(pictureZoom1)); 
			break;
		case TakePicture.CAMERAZOOM:
			File pictureZoom = new File(TakePicture.TEMP_PATH);
			mTakePicture.startPhotoZoom(Uri.fromFile(pictureZoom)); 
			break;
		case TakePicture.PHOTO:
			if(data!= null && data.getData() != null){
				Bundle extrass = data.getExtras();  
				if (extrass != null) {  
					Bitmap photo = extrass.getParcelable("data");  
					ByteArrayOutputStream stream = new ByteArrayOutputStream();  
					photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);// (0 - 100)压缩文件  
					_photo.setImageBitmap(photo);  
				}else{
					ToolBox.showToast(this, "extrass is TM null de !");
				}  
			}
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
	                Bitmap photo = extras.getParcelable("data");
	                ByteArrayOutputStream stream = new ByteArrayOutputStream();  
	                photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);// (0 - 100)压缩文件  
	                
	        		try {
	        			//多做一步删除操作 放置将拍照的源文件上传.
	        			CacheFileUtil.deleteFile(CacheFileUtil.getPicturePath() + TakePicture.TEMP_NAME);
	        			CacheFileUtil.SavePicture(TakePicture.TEMP_NAME, photo);
	        			if(photo != null){
	        				_photo.setImageBitmap(photo);  
						}
	        			Log.i(TAG, "flag2:" + CacheFileUtil.isExistFile(TakePicture.TEMP_PATH));
					} catch (IOException e) {
						e.printStackTrace();
					}
	            }  
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
		 */
}
