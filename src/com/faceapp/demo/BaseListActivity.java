package com.faceapp.demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.faceapp.demo.adapter.FacePlusAdapter;
import com.faceapp.demo.http.FacePlusAPI;
import com.faceapp.demo.http.HttpManager;
import com.faceapp.demo.http.HttpParameters;
import com.faceapp.demo.http.HttpTask;
import com.faceapp.demo.object.Face_Info;
import com.faceapp.demo.object.Group;
import com.faceapp.demo.object.Person;
import com.faceapp.demo.object.Return_Boolean;
import com.faceapp.demo.object.Return_Groups;
import com.faceapp.demo.util.ToolHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class BaseListActivity<T> extends BaseActivity implements OnItemClickListener, OnClickListener, OnItemLongClickListener{

	private ListView mListView ;
	private List<T> mList;
	private FacePlusAdapter<T> mAdapter;
	
	private TextView nameView ;
	private View btn_create , btn_delete ;
	private String delete_ID ;
	private int Type ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		onRefresh();
	}
	
	@Override
	protected void initView() {
		setContentView(R.layout.view_action);
		
		Type = getIntent().getIntExtra(FacePlusAPI.Type, 0);
		setTitle(String.valueOf(Type));
		
		mListView = (ListView)findViewById(R.id.listview);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
		mList = new ArrayList<T>();
		mAdapter = new FacePlusAdapter<T>(mContext, mList);
		mListView.setAdapter(mAdapter);
		
		btn_create = findViewById(R.id.create);
		btn_delete = findViewById(R.id.delete);
		btn_create.setOnClickListener(this);
		btn_delete.setOnClickListener(this);
		
		nameView = (TextView)findViewById(R.id.name);
		super.initView();
	}
	
	@Override
	protected void onRefresh() {
		String api = "";
		HttpParameters mParameters = new HttpParameters().getFaceParameters();
		switch (Type) {
		case FacePlusAPI.Type_Group:
			api = FacePlusAPI.Group_Get_List ;
			break;
		case FacePlusAPI.Type_Person:
			api = FacePlusAPI.Group_Getinfo ;
			mParameters.add(FacePlusAPI.GROUP_ID, getIntent().getStringExtra(FacePlusAPI.GROUP_ID));
			break;
		case FacePlusAPI.Type_Face:
			api = FacePlusAPI.Person_Get_Info ;
			mParameters.add(FacePlusAPI.PERSON_ID, getIntent().getStringExtra(FacePlusAPI.PERSON_ID));
			break;
		}
		new HttpTask(this, api, mParameters, HttpManager.HTTPMETHOD_GET, null, geDatatListener, getString(R.string.hint_loading)).execute("");
		super.onRefresh();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.create:
			String api_ = "";
			switch (Type) {
				case FacePlusAPI.Type_Group:
					api_ = FacePlusAPI.Group_Create ;
					dialog_Create(this, "创建Group", api_);
					break;
				case FacePlusAPI.Type_Person:
					api_ = FacePlusAPI.Person_Create ;
					dialog_Create(this, "创建Person", api_);
					break;
				case FacePlusAPI.Type_Face:
					Intent mIntent = new Intent(this , PictureDetect.class);
					mIntent.putExtra(FacePlusAPI.PERSON_ID, getIntent().getStringExtra(FacePlusAPI.PERSON_ID));
					startActivity(mIntent);
					break;
			}
			break;
		case R.id.delete:
			if(ToolHelper.isEmpty(delete_ID)){
				ToolHelper.toast(mContext, "通过长按选择要删除的条目.");
			}else{
				String api = "";
				HttpParameters parameters = new HttpParameters().getFaceParameters();
				switch (Type) {
				case FacePlusAPI.Type_Group:
					parameters.add(FacePlusAPI.GROUP_ID, delete_ID);
					api = FacePlusAPI.Group_Delete ;
					break;
				case FacePlusAPI.Type_Person:
					parameters.add(FacePlusAPI.PERSON_ID, delete_ID);
					api = FacePlusAPI.Person_Delete ;
					break;
				case FacePlusAPI.Type_Face:
					parameters.add(FacePlusAPI.PERSON_ID, getIntent().getStringExtra(FacePlusAPI.PERSON_ID));
					parameters.add(FacePlusAPI.Face_ID, delete_ID);
					parameters.add(FacePlusAPI.PERSON_ID, getIntent().getStringExtra(FacePlusAPI.PERSON_ID));
					api = FacePlusAPI.Person_Remove_Face ;
					break;
				}
				new HttpTask(this, api, parameters,HttpManager.HTTPMETHOD_GET, null, deleteListener, getString(R.string.hint_loading)).execute("");
			}
			break;
		}
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent mIntent ;
		switch (Type) {
		case FacePlusAPI.Type_Group:
			mIntent = new Intent(mContext, Activity_PersonList.class);
			mIntent.putExtra(FacePlusAPI.Type, FacePlusAPI.Type_Person);
			mIntent.putExtra(FacePlusAPI.GROUP_ID, ((Group)mList.get(position)).group_id);
			startActivity(mIntent);
			break;
		case FacePlusAPI.Type_Person:
			mIntent = new Intent(mContext, Activity_FaceList.class);
			mIntent.putExtra(FacePlusAPI.Type, FacePlusAPI.Type_Face);
			mIntent.putExtra(FacePlusAPI.PERSON_ID, ((Person)mList.get(position)).person_id);
			startActivity(mIntent);
			break;
		case FacePlusAPI.Type_Face:
			mIntent = new Intent(mContext, Activity_Face.class);
			mIntent.putExtra(FacePlusAPI.Face_ID, ((Face_Info)mList.get(position)).face_id);
			startActivity(mIntent);
			break;
		}
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		switch (Type) {
		case FacePlusAPI.Type_Group:
			delete_ID =  ((Group)mList.get(position)).group_id ;
			break;
		case FacePlusAPI.Type_Person:
			delete_ID =  ((Person)mList.get(position)).person_id ;
			break;
		case FacePlusAPI.Type_Face:
			delete_ID =  ((Face_Info)mList.get(position)).face_id ;
			break;
		}
		setTitle("当前选择的:"+delete_ID);
		return true;
	}
	
	HttpTask.NetRequestListener geDatatListener = new HttpTask.NetRequestListener() {
		@Override
		public Object onRequest() {return null;}
		@Override
		public void onPreRequest() {}
		public void onError(String response) {};
		@Override
		public void onComplete(String response) {
			Log.i(TAG, response);
			switch (Type) {
			case FacePlusAPI.Type_Group:
				initGroup(response);
				break;
			case FacePlusAPI.Type_Person:
				initPerson(response);
				break;
			case FacePlusAPI.Type_Face:
				initFace(response);
				break;
			}
		}
		
	};
	
	public void initGroup(String response){
		Return_Groups _mGroups;
		try {
			_mGroups = new Gson().fromJson(response.trim(),new TypeToken<Return_Groups>() {}.getType());
			if (_mGroups != null && _mGroups.group != null&& _mGroups.group.size() > 0) {
				mList.clear();
				mList.addAll((Collection<? extends T>) _mGroups.group);
			} else {
				mList.clear();
				ToolHelper.toast(mContext, getString(R.string.hint_data_null));
			}
			mAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			ToolHelper.toast(mContext, getString(R.string.hint_data_error));
		}
	}
	
	public void initPerson(String response){
		Group<Person> mGroup;
		try {
			mList.clear();
			mGroup = new Gson().fromJson(response.trim(), new TypeToken<Group<Person>>() {}.getType());
			if (mGroup != null) {
				nameView.setText(mGroup.getName());
				if (mGroup.getArray() != null && mGroup.getArray().size() > 0) {
					mList.addAll((Collection<? extends T>) mGroup.getArray());
				}
			} else {
				ToolHelper.toast(mContext, getString(R.string.hint_data_null));
			}
			mAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			ToolHelper.toast(mContext, getString(R.string.hint_data_error));
		}
	}
	
	public void initFace(String response){
		Person<Group> mResult ;
		try {
			
			mResult = new Gson().fromJson(response.trim(), new TypeToken<Person<Group>>() {}.getType());
        	if(mResult != null && mResult.face_id != null && mResult.face_id.size() > 0){
        		mList.clear();
        		for (int i = 0; i < mResult.face_id.size(); i++) {
        			mList.add((T) new Face_Info(mResult.face_id.get(i)));
				}
        	}else{
        		mList.clear();
        		ToolHelper.toast(mContext, getString(R.string.hint_detect_noface));
        	}
        	mAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			Log.i(TAG, e.toString());
			ToolHelper.toast(mContext, getString(R.string.hint_data_error) + e.toString());
		}
		
	}

	/**
	 * 删除请求处理
	 */
	HttpTask.NetRequestListener deleteListener = new HttpTask.NetRequestListener() {
		@Override
		public Object onRequest() {
			return null;
		}

		@Override
		public void onPreRequest() {
		}

		@Override
		public void onComplete(String response) {
			Return_Boolean result;
			try {
				result = new Gson().fromJson(response.trim(),new TypeToken<Return_Boolean>() {}.getType());
				if (result != null && result.success ) {
					ToolHelper.toast(mContext, getString(R.string.hint_action_success));
					onRefresh();
				} else {
					ToolHelper.toast(mContext, getString(R.string.hint_action_fail));
				}
			} catch (Exception e) {
				ToolHelper.toast(mContext, getString(R.string.hint_action_fail));
			}
		}
		public void onError(String response) {};
	};

	/**
	 * 创建Group/Person 对话框.
	 */
	public void dialog_Create(final Context context , final String title ,final String api ) {
		LayoutInflater factory = LayoutInflater.from(context);
		View dialog_View = factory.inflate(R.layout.compoent_create_dialog,null);
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(dialog_View);

		final TextView titleView = (TextView) dialog_View.findViewById(R.id.title);
		final EditText contentView = (EditText) dialog_View.findViewById(R.id.content);

		titleView.setText(title);

		Button positiveButton = (Button) dialog_View.findViewById(R.id.dialog_positiveButton);
		Button negativeButton = (Button) dialog_View.findViewById(R.id.dialog_negativeButton);

		// static private char[] alphabet =
		positiveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String str = contentView.getText().toString().trim();
				HttpParameters mParameters = new HttpParameters().getFaceParameters();
				switch (Type) {
				case FacePlusAPI.Type_Group:
					mParameters.add(FacePlusAPI.GROUP_NAME, str);
					break;
				case FacePlusAPI.Type_Person:
					mParameters.add(FacePlusAPI.PERSON_NAME, str);
					mParameters.add(FacePlusAPI.GROUP_ID, getIntent().getStringExtra(FacePlusAPI.GROUP_ID));
					break;
				case FacePlusAPI.Type_Face:
					break;
				}
				dialog.dismiss();
				new HttpTask(mContext, api,mParameters, HttpManager.HTTPMETHOD_GET, null,
						new HttpTask.NetRequestListener() {
							@Override
							public Object onRequest() {return null;}
							@Override
							public void onPreRequest() {}
							@Override
							public void onError(String response) {}
							@Override
							public void onComplete(String response) {
								T _mGroups;
								try {
									_mGroups = new Gson().fromJson(response.trim(),new TypeToken<T>() {}.getType());
									if (_mGroups != null) {
										onRefresh();
										ToolHelper.toast(mContext,getString(R.string.hint_create_success));
									} else {
										ToolHelper.toast(mContext,getString(R.string.hint_create_error));
									}
								} catch (Exception e) {
									ToolHelper.toast(mContext,getString(R.string.hint_create_error));
								}
							}
						}, getString(R.string.hint_create)).execute("");
			}
		});
		negativeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

}
