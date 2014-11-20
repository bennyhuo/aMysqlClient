package com.piglet.ui;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.piglet.App;
import com.piglet.R;
import com.piglet.adapter.TbAdapter;
import com.piglet.dao.pojo.Table;
import com.piglet.utils.Constant;
import com.umeng.analytics.MobclickAgent;

public class TbActivity extends Activity {
	private String dbname;
	private ListView listView;
	private TbAdapter adapter;
	private TextView tips;

	private ProgressBar progressBar;

	public static final int WHAT_TABLE_RETURNED = 0;
	public static final int WHAT_TABLE_EMPTY = 1;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.value.WHAT_DATA_RETURNED:
				tips.setVisibility(View.GONE);
				progressBar.setVisibility(View.GONE);
				List<Table> list = (List<Table>) msg.obj;
				adapter.addAll(list);
				break;
			case Constant.value.WHAT_DATA_EMPTY:
				tips.setText(R.string.empty_databases);
				progressBar.setVisibility(View.GONE);
				break;
			case Constant.value.WHAT_FAILED_TO_CONNECT:
				progressBar.setVisibility(View.GONE);
				tips.setText(R.string.fail_to_connect);
				break;
			}

			super.handleMessage(msg);
		}

	};

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            If the activity is being re-initialized after previously being
	 *            shut down then this Bundle contains the data it most recently
	 *            supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it
	 *            is null.</b>
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tb);

		dbname = getIntent().getStringExtra(Constant.key.DATABASE);

		tips = (TextView) findViewById(R.id.tips);
		tips.setText(R.string.show_db_working);

		progressBar = (ProgressBar) findViewById(R.id.progressbar);
		listView = (ListView) findViewById(R.id.tblist);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				view.showContextMenu();
			}
		});

		adapter = new TbAdapter(this);
		listView.setAdapter(adapter);
		registerForContextMenu(listView);
		initData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(com.piglet.R.menu.main, menu);
		return true;
	}

	private void initData() {
		App.get().selectDb(dbname, handler);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.tablelist, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.data:
			Intent it = new Intent();
			it.setClass(getApplicationContext(), DataActivity.class);
			it.putExtra(Constant.key.TABLE, adapter.getItemObject(info.position).getName());
			startActivity(it);
			break;
		case R.id.schema:
			Intent it1 = new Intent();
			it1.setClass(getApplicationContext(), PropActivity.class);
			it1.putExtra(Constant.key.TABLE, adapter.getItemObject(info.position).getName());
			startActivity(it1);
			break;
		}
		return true;
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
