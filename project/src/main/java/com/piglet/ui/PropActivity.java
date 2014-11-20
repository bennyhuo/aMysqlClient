package com.piglet.ui;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.piglet.App;
import com.piglet.R;
import com.piglet.adapter.DbAdapter;
import com.piglet.adapter.PropAdapter;
import com.piglet.adapter.TbAdapter;
import com.piglet.dao.pojo.ConnectionInfo;
import com.piglet.dao.pojo.Table;
import com.piglet.dao.pojo.TableProperty;
import com.piglet.utils.Constant;
import com.umeng.analytics.MobclickAgent;

public class PropActivity extends Activity {
	private String tablename;
	private ListView listView;
	private PropAdapter adapter;
	private TextView tips;

	private ProgressBar progressBar;

	public static final int WHAT_PROPERTY_RETURNED = 0;
	public static final int WHAT_PROPERTY_EMPTY = 1;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.value.WHAT_DATA_RETURNED:
				tips.setVisibility(View.GONE);
				progressBar.setVisibility(View.GONE);
				List<TableProperty> list = (List<TableProperty>) msg.obj;
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
		setContentView(R.layout.activity_prop);

		tablename = getIntent().getStringExtra(Constant.key.TABLE);

		tips = (TextView) findViewById(R.id.tips);
		tips.setText(R.string.show_db_working);

		progressBar = (ProgressBar) findViewById(R.id.progressbar);
		listView = (ListView) findViewById(R.id.proplist);
		adapter = new PropAdapter(this);
		listView.setAdapter(adapter);
		initData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(com.piglet.R.menu.main, menu);
		return true;
	}

	private void initData() {
		App.get().descTable(tablename, handler);
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
