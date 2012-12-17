package com.faceapp.demo;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
 * Face(新增/修改/删除)
 * @author Will
 */
public class Activity_Person_Detail extends BaseActivity implements OnClickListener, OnItemClickListener, OnItemLongClickListener {

	private String Person_ID , Face_ID;
	private Person<Group> mPerson;

	private ListView mListView;
	private ArrayAdapter<String> mFaceAdapter;
	private ArrayList<String> mFaces;

	private SharedPreferencesHelper spHelper;

	private TextView person_name;
	private View person_add, person_delete, person_edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.view_person_detail);
		spHelper = new SharedPreferencesHelper(this);
		initView();

		Person_ID = getIntent().getStringExtra(FacePlusAPI.PERSON_ID);
		if (!ToolHelper.isEmpty(Person_ID)) {// 指定查看
			onRefresh();
		} else if (!ToolHelper.isEmpty(spHelper.getPersonId())) {// 查看自己
			Person_ID = spHelper.getPersonId();
			onRefresh();
		}
	}

	@Override
	protected void initView() {

		person_name = (TextView) findViewById(R.id.name);

		person_add = findViewById(R.id.create);
		person_delete = findViewById(R.id.delete);
		person_edit = findViewById(R.id.edit);

		person_add.setOnClickListener(this);
		person_delete.setOnClickListener(this);
		person_edit.setOnClickListener(this);

		mListView = (ListView) findViewById(R.id.listview);

		mFaces = new ArrayList<String>();
		mFaceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, mFaces);
		mListView.setAdapter(mFaceAdapter);

		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);

		super.initView();
	}

	protected void setView() {
		person_name.setText(mPerson.person_name);
		if (mPerson.face_id != null && mPerson.face_id.size() > 0) {
			mFaces.clear();
			mFaces.addAll(mPerson.face_id);
			mFaceAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onRefresh() {
		HttpParameters mParameters = new HttpParameters().getFaceParameters();
		mParameters.add(FacePlusAPI.PERSON_ID, Person_ID);
		new HttpTask(this, FacePlusAPI.Person_Get_Info, mParameters, HttpManager.HTTPMETHOD_GET, null, getListener, getString(R.string.hint_loading)).execute("");
		super.onRefresh();
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
			try {
				mPerson = new Gson().fromJson(response.trim(), new TypeToken<Person<Group>>() {}.getType());
				if (mPerson != null) {
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.create:
			Intent mIntent = new Intent(this , PictureDetect.class);
			mIntent.putExtra(FacePlusAPI.PERSON_ID, Person_ID);
			startActivity(mIntent);
			break;
		case R.id.delete:
			if(ToolHelper.isEmpty(Face_ID)){
				ToolHelper.toast(mContext, "通过长按选择要删除的条目.");
			}else{
				HttpParameters parameters = new HttpParameters().getFaceParameters();
				parameters.add(FacePlusAPI.PERSON_ID, Person_ID);
				parameters.add(FacePlusAPI.Face_ID, Face_ID);
				new HttpTask(this, FacePlusAPI.Person_Remove_Face,parameters,HttpManager.HTTPMETHOD_GET, null, deleteListener, getString(R.string.hint_loading)).execute("");
			}
			break;
		case R.id.edit:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent mIntent = new Intent(mContext, Activity_Face.class);
		mIntent.putExtra(FacePlusAPI.Face_ID, mFaceAdapter.getItem(position));
		startActivity(mIntent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		Face_ID = mFaceAdapter.getItem(position);
		setTitle("当前选中:" + Face_ID);
		return true;
	}

}
