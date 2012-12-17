package com.faceapp.demo;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.faceapp.demo.object.Group;
import com.faceapp.demo.object.Person;
import com.faceapp.demo.util.SharedPreferencesHelper;
import com.faceapp.demo.util.ToolHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * User(新增/修改/删除)
 * @author Will
 */
public class Activity_Group_Detail extends BaseActivity implements OnItemClickListener, OnClickListener, OnItemLongClickListener {

	private TextView nameView;
	private ListView mListView;
	private Group<Person> mGroup;

	private FacePlusAdapter<Person> mAdapter;
	private ArrayList<Person> mPersons;
	private SharedPreferencesHelper spHelper ;
	
	private String Group_ID , Person_ID;
	private View group_add, group_delete, group_edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.view_group_detail);
		Group_ID = getIntent().getStringExtra(FacePlusAPI.GROUP_ID);
		spHelper = new SharedPreferencesHelper(this);

		initView();
		onRefresh();
	}

	@Override
	protected void initView() {

		nameView = (TextView) findViewById(R.id.name);
		mListView = (ListView) findViewById(R.id.listview);
		mPersons = new ArrayList<Person>();
		mAdapter = new FacePlusAdapter<Person>(this, mPersons);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
		
		group_add = findViewById(R.id.create);
		group_delete = findViewById(R.id.delete);
		group_edit = findViewById(R.id.edit);

		group_add.setOnClickListener(this);
		group_delete.setOnClickListener(this);
		group_edit.setOnClickListener(this);

		super.initView();
	}

	protected void setView() {
		nameView.setText(mGroup.getName());
		if (mGroup.getArray() != null && mGroup.getArray().size() > 0) {
			mPersons.clear();
			mPersons.addAll(mGroup.getArray());
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onRefresh() {
		HttpParameters mParameters = new HttpParameters().getFaceParameters();
		mParameters.add(FacePlusAPI.GROUP_ID, Group_ID);
		new HttpTask(this, FacePlusAPI.Group_Getinfo, mParameters, HttpManager.HTTPMETHOD_GET, null, getListener, getString(R.string.hint_loading)).execute("");
		super.onRefresh();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent mIntent = new Intent(mContext , Activity_Person_Detail.class);
		mIntent.putExtra(FacePlusAPI.PERSON_ID, mAdapter.getItem(position).getID());
		startActivity(mIntent);
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		Person_ID = mAdapter.getItem(position).getID();
		setTitle("当前选中:" + Person_ID);
		return true;
	}
	
	/**
	 * 创建用户.
	 * @param context
	 */
	public void Dialog_Create(final Context context ){
		LayoutInflater factory = LayoutInflater.from(context);
		View dialog_View = factory.inflate(R.layout.compoent_create_dialog, null);
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(dialog_View);
		
		final TextView titleView = (TextView)dialog_View.findViewById(R.id.title);
		final EditText contentView = (EditText)dialog_View.findViewById(R.id.content);
		
		titleView.setText("创建用户");
		
		Button positiveButton = (Button)dialog_View.findViewById(R.id.dialog_positiveButton);
		Button negativeButton = (Button)dialog_View.findViewById(R.id.dialog_negativeButton);

		//static private char[] alphabet =
		positiveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			    String str = contentView.getText().toString().trim();
			    
			    HttpParameters mParameters = new HttpParameters().getFaceParameters() ;
			    mParameters.add(FacePlusAPI.PERSON_NAME, str);
			    mParameters.add(FacePlusAPI.GROUP_ID, Group_ID);

			    new HttpTask(mContext, FacePlusAPI.Person_Create, mParameters , HttpManager.HTTPMETHOD_GET, null, new HttpTask.NetRequestListener() {
			        @Override
			        public Object onRequest() {
			    	return null;
			        }
			        @Override
			        public void onPreRequest() {
			        }
			        @Override
			        public void onComplete(String response) {
			        	Person mPerson ;
						try {
							mPerson = new Gson().fromJson(response.trim(),new TypeToken<Person>() {}.getType());
							if(mPerson != null ){
								spHelper.setPersonId(mPerson.person_id);
								onRefresh();
								ToolHelper.toast(mContext, getString(R.string.hint_create_success));
							}else{
								ToolHelper.toast(mContext, getString(R.string.hint_create_error));
							}
						} catch (Exception e) {
							ToolHelper.toast(mContext, getString(R.string.hint_create_error));
						}
						dialog.dismiss();
			        }
			        @Override
			        public void onError(String response) {}
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.create:
			Dialog_Create(this);
			break;
		case R.id.delete:
			if(ToolHelper.isEmpty(Person_ID)){
				ToolHelper.toast(mContext, "通过长按选择要删除的条目.");
			}else{
				HttpParameters parameters = new HttpParameters().getFaceParameters();
				parameters.add(FacePlusAPI.PERSON_ID, Person_ID);
				new HttpTask(this, FacePlusAPI.Person_Delete,parameters,HttpManager.HTTPMETHOD_GET, null, deleteListener, getString(R.string.hint_loading)).execute("");
			}
			break;
		case R.id.edit:
			break;
		}
	}
	
	HttpTask.NetRequestListener getListener = new HttpTask.NetRequestListener() {
		@Override
		public Object onRequest() {
			return null;
		}

		@Override
		public void onPreRequest() {
		}

		@Override
		public void onComplete(String response) {
			Group<Person> _mGroup;
			try {
				_mGroup = new Gson().fromJson(response.trim(), new TypeToken<Group<Person>>() {
				}.getType());
				if (_mGroup != null) {
					mGroup = _mGroup;
					setView();
				} else {
					ToolHelper.toast(mContext, getString(R.string.hint_data_null));
				}
			} catch (Exception e) {
				ToolHelper.toast(mContext, getString(R.string.hint_data_error));
			}
		}
		public void onError(String response) {};
	};

}
