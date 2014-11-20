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
import com.piglet.dao.pojo.ConnectionInfo;
import com.piglet.dao.pojo.Database;

public class DbAdapter extends BaseAdapter {

	private Context context;
	
	private LinkedList<Database> list = new LinkedList<Database>();
	
	private LayoutInflater inflater;

	public DbAdapter(Context context) {
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
			convertView = inflater.inflate(R.layout.db_item, null);
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

	public void add(Database db) {
		if(db == null){
			return;
		}
		this.list.add(db);
		this.notifyDataSetChanged();
	}

	public void addAll(List<Database> dbList){
		if(dbList == null){
			return ;
		}
		this.list.addAll(dbList);
		this.notifyDataSetChanged();
	}
	
	public void clear(){
		this.list.clear();
		this.notifyDataSetChanged();
	}
	
	public Database getItemObject(int position){
		Database db = new Database(list.get(position));
		return db;
	}
}
