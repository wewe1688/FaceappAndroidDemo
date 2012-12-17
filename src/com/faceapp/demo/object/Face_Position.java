package com.faceapp.demo.object;

/**
 * 人脸在图片的位置信息.
 * @author Will
 */
public class Face_Position {
	
	public float width ;
	public float height ;
	
	public location center ;
	public location eye_left ;
	public location eye_right ;
	public location mouth_left ;
	public location mouth_right ;
	
	public class location{
		public float x ;
		public float y ;
	}

}
