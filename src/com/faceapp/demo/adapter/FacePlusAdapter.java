package com.faceapp.demo.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.faceapp.demo.R;
import com.faceapp.demo.object.Base;

/**
 * @author Will
 * @param <Group>
 */
public class FacePlusAdapter<T> extends ArrayAdapter<T>{

	private LayoutInflater mInflater ;
	
	public FacePlusAdapter(Context context, List<T> objects) {
		super(context, 0, 0, objects);
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_group_view, null);
			holder = new ViewHolder();
			holder.group_name = (TextView) convertView.findViewById(R.id.group_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		Base<Object> mGroup = (Base<Object>)getItem(position);
		holder.group_name.setText(mGroup.getName());
		
		return convertView;
	}
	
	class ViewHolder{
		TextView group_name ;
	}

}
