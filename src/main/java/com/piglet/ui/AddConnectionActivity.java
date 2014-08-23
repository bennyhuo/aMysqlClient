package com.piglet.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.piglet.utils.MysqlUtils;
import com.umeng.analytics.MobclickAgent;

public class AddConnectionActivity extends Activity {
	public static final int RESULT_CODE_CONN = 1;

	private EditText nameInput;
	private EditText hostInput;
	private EditText portInput;
	private EditText dbnameInput;
	private EditText usernameInput;
	private EditText passwordInput;

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

		nameInput = (EditText) findViewById(R.id.name);
		hostInput = (EditText) findViewById(R.id.host);
		portInput = (EditText) findViewById(R.id.port);
		dbnameInput = (EditText) findViewById(R.id.db);
		usernameInput = (EditText) findViewById(R.id.user);
		passwordInput = (EditText) findViewById(R.id.password);

		Button btn = (Button) findViewById(R.id.add_btn);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String name = nameInput.getText().toString();
				final String host = hostInput.getText().toString();
				final int port = Integer.valueOf(portInput.getText().toString());
				final String dbname = dbnameInput.getText().toString();
				final String username = usernameInput.getText().toString();
				final String password = passwordInput.getText().toString();

				if (MysqlUtils.checkHost(host) && MysqlUtils.checkDbName(dbname) && MysqlUtils.checkUserName(username) && MysqlUtils.checkPassword(password)) {

					final ConnectionInfoDao dao = new ConnectionInfoDao();
					final ConnectionInfo info = dao.findById(name);
					if (info == null) {
						ConnectionInfo info1 = new ConnectionInfo(name, host, port, dbname, username, password);
						dao.save(info1);
						Toast.makeText(getApplicationContext(), R.string.add_conn_succ, Toast.LENGTH_LONG).show();
						back();
					} else {
						Context ctx = AddConnectionActivity.this;
						AlertDialog.Builder builder = new AlertDialog.Builder(ctx).setTitle(String.format(ctx.getString(R.string.if_update_conn), name))
								.setMessage(R.string.if_update_content);

						builder.setNegativeButton(R.string.change_name, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						});

						builder.setPositiveButton(R.string.update_conn, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								info.setHost(host);
								info.setPort(port);
								info.setUsername(username);
								info.setPassword(password);
								info.setDbname(dbname);
								dao.update(info);
								Toast.makeText(getApplicationContext(), R.string.update_conn_succ, Toast.LENGTH_LONG).show();
								back();
							}
						});

						AlertDialog dialog = builder.create();
						dialog.show();
					}

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
