package com.faceapp.demo.object;

import java.util.List;


public class Image extends Base<Object>{
	public String url ;
	public String img_id ;
	public List<Face_Position> face ;
	
	@Override
	public String getID() {
		return img_id;
	}
	
	@Override
	public List<Object> getArray() {
		// TODO Auto-generated method stub
		return super.getArray();
	}
}
