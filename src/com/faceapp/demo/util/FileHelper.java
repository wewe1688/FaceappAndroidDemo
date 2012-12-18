package com.faceapp.demo.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

/**
 * 文件操作类
 * 头像操作类. 1.检测本地是否存在 2.是否保存头像 3.显示Url还是文件
 * @author Will
 * 
 */
public class FileHelper {

	public final static String DIR = "FacePlus";
	
	public final static String IMAGECACHE = "cache_image";
	public final static String RESUMECACHE = "cache_date";
	
	public final static String CACHE = "cache";
	public final static String HEADSUFFIX = ".png";

	private final static String TAG = "CacheFileUtil";

	/**
	 * 说明SDCard存在,且可使用.
	 * 
	 * @return
	 */
	public static boolean isExistSD() {
		/* 具有读/写权限 */
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			return true;
		}
		return false;
	}

	/**
	 * @return
	 */
	public static String getPicturePath() {
		String directory = Environment.getExternalStorageDirectory().getPath()
				+ "/" + DIR + "/" + IMAGECACHE + "/";
		File dir = new File(directory);
		if (!dir.exists()) {
			if (dir.mkdirs()) {
				Log.i(TAG, "目录创建成功");
			} else {
				Log.i(TAG, "目录创建失败");
			}
		}
		return directory;
	}
	
    /**   
     * 删除单个文件   
     * @param   fileName    被删除文件的文件名   
     * @return 单个文件删除成功返回true,否则返回false   
     */    
    public static boolean deleteFile(String fileName){     
        File file = new File(fileName);     
        if(file.isFile() && file.exists()){     
            file.delete();     
            return true;     
        }else{     
            return false;     
        }     
    } 
    
	/**
	 * 清理缓存
	 * 
	 * @param cachePath
	 * @return
	 * @throws Exception
	 */
	public static boolean clearCachePath4Max(String cachePath, int MAX_M)
			throws Exception {
		long size = 0;
		File f = new File(cachePath);
		if (f.isDirectory()) {
			File flist[] = f.listFiles();
			for (int i = 0; i < flist.length; i++) {
				if (flist[i].isDirectory()) {
					size = size + getFileSize(flist[i]);
				} else {
					size = size + flist[i].length();
				}
			}
			if (size > 1048576 * MAX_M) {
				return deleteFile(f);
			}
		}
		return false;
	}
	
	public static long getFileSize(File f) throws Exception {// 取得文件大小
		long s = 0;
		if (f.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(f);
			s = fis.available();
			fis.close();
		}
		return s;
	}

	/**
	 * 删除文件夹以及内所有文件
	 * 
	 * @param file
	 */
	public static boolean deleteFile(File file) {
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // delete()方法 你应该知道 是删除的意思;
			} else if (file.isDirectory()) { // 否则如果它是一个目录
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
				}
			}
			file.delete();
			System.out.println("指定File已成功删除！");
			return true;
		} else {
			System.out.println("指定删除的File不存在！");
			return false;
		}
	}


	/**
	 * 保存图片
	 * 
	 * @param bitName
	 * @param mBitmap
	 * @throws IOException
	 */
	public static String savePicture(String dir , String fileName, Bitmap mBitmap)
			throws IOException {
		String filePath = dir + fileName;
		if (!(new File(filePath)).exists()) {
			FileOutputStream fOut = null;
			try {
				fOut = new FileOutputStream(filePath);
				mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
				try {
					fOut.flush();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
				try {
					fOut.close();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
				return filePath;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return filePath;
		}
	}

	/**
	 * 保存图片
	 * @param bitName
	 * @param mBitmap
	 * @throws IOException
	 */
	public static void savePhoto(String filePath, Bitmap mBitmap) {
		//if (!(new File(filePath)).exists()) {
			FileOutputStream fOut = null;
			try {
				fOut = new FileOutputStream(filePath);
				mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
				try {
					fOut.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					fOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		//} else {
		//}
	}
	

	/**
	 * 读取图片文件
	 * 
	 * @param filePath
	 * @return
	 */
	public static Bitmap readPicture(String filePath) {
		File pic = new File(filePath);
		if (pic.exists()) {
			return BitmapFactory.decodeFile(filePath);
		} else {
			Log.i(TAG, "picture is no find");
			return null;
		}
	}

	/**
	 * 检测文件是否存在
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean isExistFile(String fileName) {
		File file = new File(fileName);
		return file.exists() ? true : false;
	}

	/**
	 * 生成时间图片名
	 */
	public static String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}
	
	/**
	 * 
	 * @param in
	 * @param Path
	 * @return
	 * @throws IOException
	 */
	public static Boolean saveFile(InputStream in , String Path) throws IOException{
			FileOutputStream f = new FileOutputStream(new File(Path));
			byte[] buffer = new byte[1024];
			int lenght = 0;
			while ( (lenght = in.read(buffer)) > 0 ) {
				f.write(buffer,0, lenght);
			}
			f.close();
		return true;
	}
	
	/**
	 * 下载图片
	 * @param url
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static Boolean downloadFile(String url , String path , String fileName) throws IOException{
		URL u = new URL(url);
		HttpURLConnection c = (HttpURLConnection) u.openConnection();
		c.setRequestMethod("GET");
		c.setDoOutput(true);
		c.connect();
		try{
			(new File(path)).mkdirs();
		}catch (Exception e){//Catch exception if any
			return false;
		}
		FileOutputStream f = new FileOutputStream(new File(path + fileName));
		InputStream in = c.getInputStream();

		byte[] buffer = new byte[1024];
		int lenght = 0;
		while ( (lenght = in.read(buffer)) > 0 ) {
			f.write(buffer,0, lenght);
		}
		f.close();
		return true;
	}
	
}
