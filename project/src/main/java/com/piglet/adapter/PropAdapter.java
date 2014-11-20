package com.piglet.adapter;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.piglet.R;
import com.piglet.dao.pojo.Table;
import com.piglet.dao.pojo.TableProperty;

public class PropAdapter extends BaseAdapter {

	private Context context;
	
	private LinkedList<TableProperty> list = new LinkedList<TableProperty>();
	
	private LayoutInflater inflater;

	public PropAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public String getItem(int position) {
		return list.get(position).toString();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	
	class ViewHolder{
		public TextView name;
		public TextView content;
		public ImageView icon;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.prop_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.name.setText(list.get(position).getName());
		holder.content.setText(list.get(position).getContent());
		return convertView;
	}

	public void add(TableProperty tbprop) {
		if(tbprop == null){
			return;
		}
		this.list.add(tbprop);
		this.notifyDataSetChanged();
	}

	public void addAll(List<TableProperty> tbpropList){
		if(tbpropList == null){
			return ;
		}
		this.list.addAll(tbpropList);
		this.notifyDataSetChanged();
	}
	
	public void clear(){
		this.list.clear();
		this.notifyDataSetChanged();
	}
	
	public TableProperty getItemObject(int position){
		TableProperty prop = new TableProperty(list.get(position));
		return prop;
	}
}
