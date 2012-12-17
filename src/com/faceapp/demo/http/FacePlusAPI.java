package com.faceapp.demo.http;

/**
 * http://www.faceplusplus.com/zh/docs/getting_started
 * @author Will
 */
public class FacePlusAPI {

	
	public static final String API_KEY = "XXXX"; 
	public static final String API_SECRET = "XXXX"; 
	
	/**************参数***************************/
	public static final String API_Key= "api_key"; 
	public static final String API_Secret = "api_secret"; 
	/**
	 * 检测模式可以是normal(默认) 或者 oneface 。在oneface模式中，检测器仅找出图片中最大的一张脸。
	 */
	public static final String MODE = "mode" ;
	
	/**
	 * 可以是 all(默认) 或者 none或者由逗号分割的属性列表。目前支持的属性包括：gender, age, race
	 */
	public static final String ATTRIBUTE = "attribute" ;
	
	/**
	 * 可以为图片中检测出的每一张Face指定一个不超过255字节的字符串作为tag，tag信息可以通过 /info/get_face 查询
	 */
	public static final String TAG = "tag" ;
	
	/**************Image操作***************************/
	public static final String Img_ID = "img_id";
	
	public static final String Face_ID = "face_id";
	
	/**************API***************************/
	public static final String Main = "http://api.faceplusplus.com/";
	/**
	 * 检测给定图片(Image)中的所有人脸(Face)的位置和相应的面部属性
	 * 目前面部属性包括性别(gender), 年龄(age)和种族(race)
	 */
	public static final String DETECTION_DETECT = Main + "detection/detect";
	
	/**
	 * 计算相似度.
	 */
	public static final String Face_Compare= Main + "recognition/compare";
	
	public static final String Face_Get_Img= Main + "info/get_image";
	public static final String Face_Get_Info = Main + "info/get_face";
	
	/**************Person操作***************************/
	public static final String Person_Create = Main + "person/create";
	public static final String Person_Get_Info = Main + "person/get_info";
	public static final String Person_Delete = Main + "person/delete";
	
	public static final String Person_Add_Face = Main + "person/add_face";
	public static final String Person_Remove_Face = Main + "person/remove_face";
	
	public static final String PERSON_ID = "person_id";
	
	public static final String PERSON_NAME = "person_name";
	
	/**************Group操作***************************/
	public static final String Group_Get_List = Main + "info/get_group_list";
	
	public static final String Group_Create = Main + "group/create";
	
	public static final String Group_Delete = Main + "group/delete";
	
	public static final String Group_Setinfo = Main + "group/set_info";
	
	public static final String Group_Getinfo = Main + "group/get_info";
	
	public static final String Group_Addperson = Main + "group/add_person";
	
	public static final String Group_Removeperson = Main + "group/remove_person";
	
	public static final String GROUP_ID = "group_id";
	
	public static final String GROUP_NAME = "group_name";
	
	public static int Type_Img = 0 ;
	public static int Type_Face = 1 ;
	public static int Type_Person = 2 ;
	public static int Type_Group = 3 ;
	
	
	
	
	
	
	
	
	
	
}
