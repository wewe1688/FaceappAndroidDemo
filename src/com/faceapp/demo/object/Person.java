package com.faceapp.demo.object;

import java.util.ArrayList;

public class Person<T> extends Base<T>{

	public String person_id ;
	
	public String person_name ;
	
	public String tag ;
	
	public ArrayList<String> face_id ;
	
	public ArrayList<T> group ;
	
	@Override
	public String getID() {
	return person_id;
	}
	
	@Override
	public String getName() {
	return person_name;
	}
	
	@Override
	public ArrayList<T> getArray() {
	return group;
	}

}
