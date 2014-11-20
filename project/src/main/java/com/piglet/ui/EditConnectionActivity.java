package com.piglet.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.piglet.R;
import com.piglet.dao.impl.ConnectionInfoDao;
import com.piglet.dao.pojo.ConnectionInfo;
import com.piglet.utils.Constant;
import com.piglet.utils.MysqlUtils;
import com.umeng.analytics.MobclickAgent;

public class EditConnectionActivity extends Activity {
	public static final int RESULT_CODE_CONN = 1;

	private EditText nameInput;
	private EditText hostInput;
	private EditText portInput;
	private EditText dbnameInput;
	private EditText usernameInput;
	private EditText passwordInput;

	private ConnectionInfoDao dao;
	private ConnectionInfo info;

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
		setContentView(R.layout.activity_add_conn);

		String name = getIntent().getStringExtra(Constant.key.CONNECTION_INFO_NAME);
		dao = new ConnectionInfoDao();
		info = dao.findById(name);
		if (info == null) {
			Toast.makeText(getApplicationContext(), R.string.unknown_err, Toast.LENGTH_LONG).show();
			back();
		}

		nameInput = (EditText) findViewById(R.id.name);
		nameInput.setEnabled(false);
		nameInput.setText(name);

		hostInput = (EditText) findViewById(R.id.host);
		hostInput.setText(info.getHost());

		portInput = (EditText) findViewById(R.id.port);
		portInput.setText(info.getPort() + "");

		dbnameInput = (EditText) findViewById(R.id.db);
		dbnameInput.setText(info.getDbname());

		usernameInput = (EditText) findViewById(R.id.user);
		usernameInput.setText(info.getUsername());

		passwordInput = (EditText) findViewById(R.id.password);
		passwordInput.setText(info.getPassword());

		Button btn = (Button) findViewById(R.id.add_btn);
		btn.setText(R.string.edit);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String host = hostInput.getText().toString();
				int port = Integer.valueOf(portInput.getText().toString());
				String dbname = dbnameInput.getText().toString();
				String username = usernameInput.getText().toString();
				String password = passwordInput.getText().toString();

				if (MysqlUtils.checkHost(host) && MysqlUtils.checkDbName(dbname) && MysqlUtils.checkUserName(username) && MysqlUtils.checkPassword(password)) {
					info.setHost(host);
					info.setPort(port);
					info.setUsername(username);
					info.setPassword(password);
					info.setDbname(dbname);
					dao.update(info);
					Toast.makeText(getApplicationContext(), R.string.update_conn_succ, Toast.LENGTH_LONG).show();
					back();

				} else {
					Toast.makeText(getApplicationContext(), R.string.illegal_conn_info, Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(com.piglet.R.menu.main, menu);
		return true;
	}

	private void back() {
		setResult(RESULT_CODE_CONN);
		finish();
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
