package com.piglet.ui;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.piglet.App;
import com.piglet.R;
import com.piglet.utils.Constant;
import com.piglet.utils.UnitConverter;
import com.piglet.widget.TwoDScrollView;
import com.umeng.analytics.MobclickAgent;

public class DataActivity extends Activity implements OnClickListener {
	private String tablename;
	private TableLayout table;
	private TwoDScrollView tds;
	private List<Map<String, Object>> rows;
	private TextView tips;

	private int curpage = 1;

	private int totalrecords = 0;
	private int totalpage = 0;

	private int pagesize = 20;

	private ProgressBar progressBar;

	private ImageView first;
	private ImageView pre;
	private ImageView next;
	private ImageView last;

	private TextView status;

	public static final int WHAT_DATA_RETURNED = 0;
	public static final int WHAT_DATA_EMPTY = 1;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.value.WHAT_DATA_RETURNED:
				tips.setVisibility(View.GONE);
				progressBar.setVisibility(View.GONE);
				rows = (List<Map<String, Object>>) msg.obj;
				totalrecords = msg.arg1;
				totalpage = (int) Math.ceil(totalrecords * 1f / pagesize);
				refreshUi();
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
		setContentView(R.layout.activity_data);

		tablename = getIntent().getStringExtra(Constant.key.TABLE);

		tips = (TextView) findViewById(R.id.tips);
		tips.setText(R.string.show_db_working);

		progressBar = (ProgressBar) findViewById(R.id.progressbar);
		table = (TableLayout) findViewById(R.id.table);
		tds = (TwoDScrollView) findViewById(R.id.tds);

		first = (ImageView) findViewById(R.id.first);
		pre = (ImageView) findViewById(R.id.pre);
		next = (ImageView) findViewById(R.id.next);
		last = (ImageView) findViewById(R.id.last);
		first.setOnClickListener(this);
		pre.setOnClickListener(this);
		next.setOnClickListener(this);
		last.setOnClickListener(this);

		status = (TextView) findViewById(R.id.status);

		initData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(com.piglet.R.menu.main, menu);
		return true;
	}

	private void initData() {
		progressBar.setVisibility(View.VISIBLE);
		App.get().queryTable(tablename, curpage, pagesize, handler);
	}

	private void refreshUi() {
		status.setText(String.format(getString(R.string.data_status), curpage, totalpage, totalrecords));
		table.removeAllViews();
		if (rows != null && !rows.isEmpty()) {
			int padding = UnitConverter.dip2px(this, 10);
			TableRow rowhead = new TableRow(this);
			for (final Map.Entry<String, Object> entry : rows.get(0).entrySet()) {
				TextView tv = new TextView(this);
				tv.setText(entry.getKey() + "");
				tv.setPadding(padding, padding, padding, padding);
				tv.setBackgroundResource(R.drawable.rectbghead);
				tv.setTextAppearance(getApplicationContext(), R.style.base_text_title);
				tv.setTextColor(0xff000000);
				tv.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Toast.makeText(getApplicationContext(), "标题" + entry.getKey(), Toast.LENGTH_LONG).show();
					}
				});

				rowhead.addView(tv, new TableRow.LayoutParams(-2, -1));
			}
			TableLayout.LayoutParams paramshead = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
			table.addView(rowhead, paramshead);

			int i = 0;
			for (Map<String, Object> map : rows) {
				TableRow row = new TableRow(this);
				i++;
				final int ci = i;

				int j = 0;
				for (final Map.Entry<String, Object> entry : map.entrySet()) {
					TextView tv = new TextView(this);
					j++;
					final int cj = j;

					tv.setText(entry.getValue() + "");
					tv.setPadding(padding, padding, padding, padding);
					tv.setBackgroundResource(R.drawable.rectbg);

					tv.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							String value = entry.getValue() == null ? "null" : entry.getValue().toString();
							Toast.makeText(getApplicationContext(), "(" + ci + "," + cj + ")" + value, Toast.LENGTH_LONG).show();
						}
					});

					row.addView(tv, new TableRow.LayoutParams(-2, -1));
				}

				TableLayout.LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
				table.addView(row, params);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.first:
			curpage = 1;
			if (totalpage > 1) {
				pre.setEnabled(false);
				next.setEnabled(true);
			}
			break;
		case R.id.pre:
			if (curpage > 1) {
				curpage--;
			}
			if (curpage == 1) {
				pre.setEnabled(false);
			}
			if (curpage == totalpage - 1) {
				next.setEnabled(true);
			}
			break;
		case R.id.last:
			curpage = totalpage;
			if (totalpage > 1) {
				pre.setEnabled(true);
				next.setEnabled(false);
			}
			break;
		case R.id.next:
			if (curpage < totalpage) {
				curpage++;
			}
			if (curpage == totalpage) {
				next.setEnabled(false);
			}
			if (curpage == 2) {
				pre.setEnabled(true);
			}
			break;
		}
		initData();
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
