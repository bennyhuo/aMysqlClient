package com.piglet.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.piglet.R;
import com.piglet.adapter.ConnInfoAdapter;
import com.piglet.dao.impl.ConnectionInfoDao;
import com.piglet.dao.pojo.ConnectionInfo;
import com.piglet.utils.Constant;
import com.piglet.widget.QuickAction;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

public class MainActivity extends Activity {
	public static final int REQUEST_CODE_ADD_CONN = 0;
	public static final int REQUEST_CODE_EDIT_CONN = 1;

	private ListView listView;
	private ConnInfoAdapter adapter;
	private Button addBtn;
	private ImageView purpleImg;
	private ImageView main_menu;
	private ConnectionInfoDao connDao;

	private FeedbackAgent agent;

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
		setContentView(R.layout.activity_main);

		connDao = new ConnectionInfoDao();

		// umeng feedback
		agent = new FeedbackAgent(MainActivity.this);
		agent.sync();

		// ument auto update
		UmengUpdateAgent.update(this);
		UmengUpdateAgent.setUpdateOnlyWifi(false);

		initView();
		initMenu();
		initData();

	}

	private void initMenu() {
		registerForContextMenu(listView);
	}

	@Override
	public void onContextMenuClosed(Menu menu) {
		super.onContextMenuClosed(menu);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.connlist, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.edit:
			Intent it = new Intent();
			it.setClass(getApplicationContext(), EditConnectionActivity.class);
			it.putExtra(Constant.key.CONNECTION_INFO_NAME, adapter.getItemObject(info.position).getName());
			startActivityForResult(it, REQUEST_CODE_EDIT_CONN);
			break;
		case R.id.del:
			connDao.delete(adapter.getItemObject(info.position));
			initData();
			break;
		case R.id.normalmode:
			Intent it1 = new Intent();
			it1.setClass(getApplicationContext(), DbActivity.class);
			it1.putExtra(Constant.key.CONNECTION_INFO, adapter.getItemObject(info.position));
			startActivity(it1);
			break;
		case R.id.cmdmode:
			Intent it2 = new Intent();
			it2.setClass(getApplicationContext(), CmdActivity.class);
			it2.putExtra(Constant.key.CONNECTION_INFO, adapter.getItemObject(info.position));
			startActivity(it2);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == AddConnectionActivity.RESULT_CODE_CONN) {
			initData();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(com.piglet.R.menu.main, menu);
		return true;
	}

	private void initView() {
		main_menu = (ImageView) findViewById(R.id.main_menu);
		main_menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//agent.startFeedbackActivity();
				showMenu();
			}
		});

		purpleImg = (ImageView) findViewById(R.id.purplecow);
		purpleImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse("http://purplecow.me"));
				startActivity(it);
			}
		});
		addBtn = (Button) findViewById(R.id.add_btn);
		addBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent it = new Intent();
				it.setClass(getApplicationContext(), AddConnectionActivity.class);
				startActivityForResult(it, REQUEST_CODE_ADD_CONN);
			}
		});

		listView = (ListView) findViewById(R.id.connlist);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				view.showContextMenu();
			}
		});

		adapter = new ConnInfoAdapter(this);
		listView.setAdapter(adapter);
	}

	private void initData() {
		adapter.clear();
		List<ConnectionInfo> list = connDao.findAll();
		adapter.addAll(list);
	}
	
	private QuickAction menuWin;
	private ArrayList<String> menuTexts;
	
	private void showMenu() {
		if (menuTexts == null) {
			// load the hint here.
			menuTexts = new ArrayList<String>();
			menuTexts.addAll(Arrays.asList(getResources().getStringArray(R.array.main_menu)));
		}
		if (menuWin != null) {
			if (menuWin.isShowing())
				return;
		} else {
			LayoutInflater inflater = LayoutInflater.from(this);
			menuWin = new QuickAction(main_menu);
			menuWin.setTitle(R.string.main_menu);

			int i = 0;
			for (String menuText : menuTexts) {
				View view = inflater.inflate(R.layout.popup_item, null);
				final TextView textView = (TextView) view.findViewById(R.id.item);
				textView.setText(menuText);
				view.setTag(i++);
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						menuWin.dismiss();
						int index =(Integer) v.getTag();
						onMenuClicked(index);
					}
				});
				menuWin.addItem(view);
			}

		}
		menuWin.show();
	}
	
	private void onMenuClicked(int index){
		switch(index){
		case 0:// fb
			agent.startFeedbackActivity();
			break;
		case 1://about
			Intent it = new Intent();
			it.setClass(MainActivity.this, AboutActivity.class);
			startActivity(it);
			break;
		}
	}
	
}
