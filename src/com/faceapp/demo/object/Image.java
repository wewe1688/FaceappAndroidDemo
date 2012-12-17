package com.faceapp.demo.object;


public class Image extends Base<Object>{
	public String url ;
	public String img_id ;
	public Face_Info face ;
	
	@Override
	public String getID() {
		return img_id;
	}
}
