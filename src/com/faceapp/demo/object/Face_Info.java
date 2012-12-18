package com.faceapp.demo.object;

public class Face_Info<T> extends Base<T>{
	public String face_id ;
	public String person_id ;
	public String person_name ;
	public String img_id ;
	public String tag ;
	public String url ;
	public Face_Position position ;
	
	/**
	 * 构造一个可以放入Adapter
	 * @param face_id
	 */
	public Face_Info(String face_id) {
		this.face_id = face_id ;
		this.person_name = face_id ;
	}
	
	@Override
	public String getID() {
		return face_id;
	}
	
	@Override
	public String getName() {
		return person_name;
	}
}
