package com.faceapp.demo.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.faceapp.demo.R;
import com.faceapp.demo.http.FacePlusAPI;

public class SharedPreferencesHelper {
	
	private SharedPreferences sp ;
	
	public SharedPreferencesHelper(Context _context) {
		sp = _context.getSharedPreferences(_context.getString(R.string.app_name), Context.MODE_WORLD_WRITEABLE);
	}

	public void setPersonId(String personid) {
		sp.edit().putString(FacePlusAPI.PERSON_ID, personid).commit();
	}
	
	public String getPersonId() {
		return sp.getString(FacePlusAPI.PERSON_ID, "");
	}
	
	public void setPersonName(String personname) {
		sp.edit().putString(FacePlusAPI.PERSON_NAME, personname).commit();
	}
	
	public String getPersonName() {
		return sp.getString(FacePlusAPI.PERSON_NAME, "");
	}
	
//	public Set<String> getImageId(){
//		return sp.getStringSet(FacePlusAPI.Img_ID, new HashSet<String>());
//	}	
//	
//	public void addImageId(String img_id){
//		Set<String> mSet = getImageId(); 
//		mSet.add(img_id);
//		
//		sp.edit().putStringSet(FacePlusAPI.Img_ID, mSet);
//	}
	
	public boolean getisLogin(){
		if(ToolHelper.isEmpty(getPersonId())){
			return false ;
		}
//		if(ToolBox.isNull(getPersonName())){
//			return false ;
//		}
		return true ;
	}


	
}
