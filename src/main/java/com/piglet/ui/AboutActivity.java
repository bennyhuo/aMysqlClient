package com.piglet.ui;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.piglet.R;
import com.piglet.core.MysqlHelper;
import com.piglet.utils.UnitConverter;
import com.umeng.analytics.MobclickAgent;

public class AboutActivity extends Activity {
	private TableLayout table;
	private List<Map<String, Object>> rows;

	public static final int WHAT_DATA_RETURNED = 0;
	public static final int WHAT_DATA_EMPTY = 1;

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
		setContentView(R.layout.activity_about);

		table = (TableLayout) findViewById(R.id.table);
		initData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(com.piglet.R.menu.main, menu);
		return true;
	}

	private void initData() {
		MysqlHelper.init(getApplicationContext());
		rows = MysqlHelper.getAbout();
		refreshUi();
	}

	private void refreshUi() {
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

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
