package com.faceapp.demo;

import java.util.ArrayList;
import java.util.List;

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
import com.faceapp.demo.object.Return_Groups;
import com.faceapp.demo.util.ToolHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 控制台Main
 * Grou(新增/修改/删除)
 * @author Will
 */
public class Activity_Group extends BaseActivity implements OnClickListener {

	private ListView mListView;
	private String Group_ID ;
	private View group_add, group_delete, group_edit;
	private List<Group> mGroups;
	private FacePlusAdapter<Group> mGroupAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_group);

		initView();
		onRefresh();
	}

	@Override
	protected void initView() {
		mListView = (ListView) findViewById(R.id.listview);
		mGroups = new ArrayList<Group>();
		mGroupAdapter = new FacePlusAdapter<Group>(this, mGroups);
		mListView.setAdapter(mGroupAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent mIntent = new Intent(mContext,
						Activity_Group_Detail.class);
				mIntent.putExtra(FacePlusAPI.GROUP_ID,
						mGroupAdapter.getItem(position).getID());
				startActivity(mIntent);
			}
		});
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Group_ID = mGroupAdapter.getItem(position).getID();
				setTitle("当前选中:" + Group_ID);
				return true;
			}
		});

		group_add = findViewById(R.id.create);
		group_delete = findViewById(R.id.delete);
		group_edit = findViewById(R.id.edit);

		group_add.setOnClickListener(this);
		group_delete.setOnClickListener(this);
		group_edit.setOnClickListener(this);

		super.initView();
	}

	@Override
	protected void onRefresh() {
		new HttpTask(this, FacePlusAPI.Group_Get_List,new HttpParameters().getFaceParameters(),
				HttpManager.HTTPMETHOD_GET, null, getListener, getString(R.string.hint_loading)).execute("");
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
			Return_Groups _mGroups;
			try {
				_mGroups = new Gson().fromJson(response.trim(),new TypeToken<Return_Groups>() {}.getType());
				if (_mGroups != null && _mGroups.group != null&& _mGroups.group.size() > 0) {
					mGroups.clear();
					mGroups.addAll(_mGroups.group);
					mGroupAdapter.notifyDataSetChanged();
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
			Dialog_Create(this);
			break;
		case R.id.delete:
			if(ToolHelper.isEmpty(Group_ID)){
				ToolHelper.toast(mContext, "通过长按选择要删除的条目.");
			}else{
				HttpParameters parameters = new HttpParameters().getFaceParameters();
				parameters.add(FacePlusAPI.GROUP_ID, Group_ID);
				new HttpTask(this, FacePlusAPI.Group_Delete,parameters,HttpManager.HTTPMETHOD_GET, null, deleteListener, getString(R.string.hint_loading)).execute("");
			}
			break;
		case R.id.edit:

			break;
		}
	}

	/**
	 * @param context
	 */
	public void Dialog_Create(final Context context) {
		LayoutInflater factory = LayoutInflater.from(context);
		View dialog_View = factory.inflate(R.layout.compoent_create_dialog,null);
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(dialog_View);

		final TextView titleView = (TextView) dialog_View
				.findViewById(R.id.title);
		final EditText contentView = (EditText) dialog_View
				.findViewById(R.id.content);

		titleView.setText("创建组");

		Button positiveButton = (Button) dialog_View
				.findViewById(R.id.dialog_positiveButton);
		Button negativeButton = (Button) dialog_View
				.findViewById(R.id.dialog_negativeButton);

		// static private char[] alphabet =
		positiveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String str = contentView.getText().toString().trim();

				HttpParameters mParameters = new HttpParameters()
						.getFaceParameters();
				mParameters.add(FacePlusAPI.GROUP_NAME, str);

				new HttpTask(mContext, FacePlusAPI.Group_Create,
						mParameters, HttpManager.HTTPMETHOD_GET, null,
						new HttpTask.NetRequestListener() {
							@Override
							public Object onRequest() {
								return null;
							}

							@Override
							public void onPreRequest() {
							}

							@Override
							public void onComplete(String response) {
								Group _mGroups;
								try {
									_mGroups = new Gson().fromJson(response.trim(),new TypeToken<Group>() {}.getType());
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
							@Override
							public void onError(String response) {
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
