package com.faceapp.demo.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.util.Log;

public class BitmapHelper {
	/**
	 * 限制最长的(宽/高)为1024.
	 */
	public static final float MAX_LENGTH = 1024 ;
	
	public static final int MAX_QUALITY = 100 ;
	
	public static final String TAG = "BitmapUtil";
	
	/**
	 * 根据文件路径读取图片
	 * @param filePath
	 * @return
	 */
	public static Bitmap getBitmap(String imgPath){
		Options options = new Options();
		//just read size
		/**
		 * decodeFile并不分配空间,但可计算出原始图片的长度和宽度,即opts.width和opts.height.
		 * 有了这两个参数,再通过一定的算法,即可得到一个恰当的inSampleSize.
		 */
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imgPath, options);
		//如果图片大于1024,进行缩放处理.
		options.inSampleSize = Math.max(1, (int)Math.ceil(Math.max((double)options.outWidth / MAX_LENGTH, (double)options.outHeight / MAX_LENGTH)));
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(imgPath, options);
	}
	
	/**
	 * 保存Bitmap
	 * @param mBitmap
	 * @param imgPath
	 */
	public static boolean saveBitmap(Bitmap mBitmap , String imgPath){
		File file = new File(imgPath);
		try {
			FileOutputStream out = new FileOutputStream(file);
			if (mBitmap.compress(Bitmap.CompressFormat.PNG, MAX_QUALITY, out)){
				out.flush();
				out.close();
				return true ;
			}		
			return false ;
		} catch (Exception e) {
			Log.i(TAG, "保存失败.Info:" + e.toString());
			return false ;
		}
	}
	
	/**
	 * 对图片进行缩放
	 * @param mBitmap
	 * @param width
	 * @param height
	 */
	public Bitmap toScale(Bitmap mBitmap , int width , int height){
			// 计算缩放比例
			float scaleWidth = ((float) width) / mBitmap.getWidth();
			float scaleHeight = ((float) height) / mBitmap.getHeight();
			// 取得想要缩放的matrix参数
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			// 得到新的图片
			return Bitmap.createBitmap(mBitmap, 0, 0, width, height, matrix,true);
	}
	
	   /**
     * make sure the color data size not more than 5M
     * 确认图片是否大于5M
     * @param rect
     * @return
     */
    public static boolean makesureSizeNotTooLarge(Rect rect) {
        final int FIVE_M = 5 * 1024 * 1024;
        if ( rect.width() * rect.height() * 2 > FIVE_M ) {
            // 不能超过5M
            return false;
        }
        return true;
    }
    
    /**
     * 自适应屏幕大小 得到最大的smapleSize
     * 同时达到此目标： 自动旋转 以适应view的宽高后, 不影响界面显示效果
     * @param vWidth view width
     * @param vHeight view height
     * @param bWidth bitmap width
     * @param bHeight bitmap height
     * @return
     */
    public static int getSampleSizeAutoFitToScreen( int vWidth, int vHeight, int bWidth, int bHeight ) {
        if( vHeight == 0 || vWidth == 0 ) {
            return 1;
        }

        int ratio = Math.max( bWidth / vWidth, bHeight / vHeight );

        int ratioAfterRotate = Math.max( bHeight / vWidth, bWidth / vHeight );

        return Math.min( ratio, ratioAfterRotate );
    }
    
    /**
     * 检测是否可以解析成位图
     * @param datas
     * @return
     */
    public static boolean verifyBitmap(byte[] datas) {
        return verifyBitmap(new ByteArrayInputStream(datas));
    }

    /**
     * 检测是否可以解析成位图
     * @param input
     * @return
     */
    public static boolean verifyBitmap(InputStream input) {
        if (input == null) {
            return false;
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        input = input instanceof BufferedInputStream ? input
                : new BufferedInputStream(input);
        BitmapFactory.decodeStream(input, null, options);
        try {
			input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return (options.outHeight > 0) && (options.outWidth > 0);
    }

    /**
     * 检测是否可以解析成位图
     * 
     * @param path
     * @return
     */
    public static boolean verifyBitmap(String path) {
        try {
            return verifyBitmap(new FileInputStream(path));
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
