package com.piglet.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.piglet.App;
import com.piglet.R;
import com.piglet.core.MysqlHelper;
import com.piglet.dao.pojo.ConnectionInfo;
import com.piglet.utils.Constant;
import com.piglet.utils.UnitConverter;
import com.piglet.widget.QuickAction;
import com.piglet.widget.ResizeLayout;
import com.piglet.widget.ResizeLayout.OnResizeListener;
import com.piglet.widget.SpaceTokenizer;
import com.piglet.widget.TwoDScrollView;
import com.umeng.analytics.MobclickAgent;

public class CmdActivity extends Activity implements OnClickListener {
	private QuickAction historyWin;
	private QuickAction commonCmdWin;
	private HorizontalScrollView scrollParent;
	private LinearLayout bottom;
	private ResizeLayout parent;
	private ConnectionInfo info;
	private TableLayout table;
	private LinearLayout table_parent;
	private TwoDScrollView tds;
	private List<Map<String, Object>> rows;
	private String updateResult;
	private TextView tips;

	private TextView text;

	private Queue<TableLayout> tableQueue;
	private ArrayList<String> history;
	private ArrayList<String> hints;
	private ArrayList<String> commonCmds;
	private ArrayAdapter<String> hintAdapter;

	private ProgressBar progressBar;

	private TextView status;
	private MultiAutoCompleteTextView cmdLine;

	private InputMethodManager imm;

	private final int WHAT_SHOW_HINT = 10;
	private final int WHAT_HIDE_HINT = 11;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.value.WHAT_DATA_RETURNED:
				tips.setVisibility(View.GONE);
				progressBar.setVisibility(View.GONE);
				rows = (List<Map<String, Object>>) msg.obj;
				updateResult = String.format(getString(R.string.query_status), msg.arg1, msg.getData().getFloat(Constant.key.QUERY_TIME));
				refreshTableUi();
				break;
			case Constant.value.WHAT_DATA_EMPTY:
				tips.setText(R.string.empty_databases);
				progressBar.setVisibility(View.GONE);
				break;
			case Constant.value.WHAT_FAILED_TO_CONNECT:
				progressBar.setVisibility(View.GONE);
				tips.setText(R.string.fail_to_connect);
				break;
			case Constant.value.WHAT_UPDATE_RETURNED:
				progressBar.setVisibility(View.GONE);
				tips.setVisibility(View.GONE);
				updateResult = String.format(getString(R.string.query_status), msg.arg1, msg.getData().getFloat(Constant.key.QUERY_TIME));
				refreshUpdateResult();
				break;
			case Constant.value.WHAT_ERROR_OCCURRED:
				progressBar.setVisibility(View.GONE);
				tips.setVisibility(View.GONE);
				updateResult = msg.obj.toString();
				refreshUpdateResult();
				break;
			case WHAT_SHOW_HINT:
				scrollParent.setVisibility(View.VISIBLE);
				break;
			case WHAT_HIDE_HINT:
				scrollParent.setVisibility(View.GONE);
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
		setContentView(R.layout.activity_cmd);

		MysqlHelper.init(this);
		
		info = (ConnectionInfo) getIntent().getSerializableExtra(Constant.key.CONNECTION_INFO);

		text = (TextView) findViewById(R.id.text);
		text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hideSoftImm();
				showHistory();
			}
		});
		
		text.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				cmdLine.setFocusableInTouchMode(true);
				cmdLine.requestFocus();
				hideSoftImm();
				showCommonCmds();
				return true;
			}
		});

		parent = (ResizeLayout) findViewById(R.id.parent);
		parent.setOnResizeListener(new OnResizeListener() {

			@Override
			public void OnResize(int w, int h, int oldw, int oldh) {
				if (h > oldh + 10) {
					handler.sendEmptyMessage(WHAT_HIDE_HINT);
				} else {
					handler.sendEmptyMessage(WHAT_SHOW_HINT);
				}
			}
		});

		scrollParent = (HorizontalScrollView) findViewById(R.id.hint_parent);

		bottom = (LinearLayout) findViewById(R.id.bottom);
		int childCount = bottom.getChildCount();
		for (int i = 0; i < childCount; i++) {
			bottom.getChildAt(i).setOnClickListener(this);
		}

		tips = (TextView) findViewById(R.id.tips);
		tips.setText(R.string.show_db_working);

		progressBar = (ProgressBar) findViewById(R.id.progressbar);
		tds = (TwoDScrollView) findViewById(R.id.tds);
		table_parent = (LinearLayout) findViewById(R.id.table_parent);
		status = (TextView) findViewById(R.id.status);
		cmdLine = (MultiAutoCompleteTextView) findViewById(R.id.cmdline);
		cmdLine.clearFocus();
		history = new ArrayList<String>();
		tableQueue = new LinkedList<TableLayout>();
		
		cmdLine.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP)
					return false;
				if (keyCode != KeyEvent.KEYCODE_ENTER)
					return false;
				scrollParent.setVisibility(View.GONE);
				hideSoftImm();
				doQuery();
				return true;
			}
		});

		cmdLine.setTokenizer(new SpaceTokenizer());
		hints = new ArrayList<String>();
		hints.addAll(Arrays.asList(getResources().getStringArray(R.array.hints)));
		hintAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, hints);
		cmdLine.setAdapter(hintAdapter);

		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

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
		App.get().connect(info);
		String sql = "show databases";
		App.get().dosql(sql, handler);
		history.add(sql);
	}

	private void doQuery() {
		String sql = cmdLine.getText().toString();
		if(TextUtils.isEmpty(sql)){
			Toast.makeText(getApplicationContext(), R.string.empty_sql, Toast.LENGTH_SHORT).show();
			return;
		}
		progressBar.setVisibility(View.VISIBLE);
		App.get().dosql(sql, handler);
		cmdLine.setText("");
		history.add(sql);
	}

	private void refreshTableUi() {
		status.setText(updateResult);
		table = new TableLayout(this);
		tableQueue.add(table);
		if(tableQueue.size() >5){
			TableLayout tableToRm = tableQueue.poll();
			table_parent.removeView(tableToRm);
			table_parent.removeView((TextView)tableToRm.getTag());
		}

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
						// Toast.makeText(getApplicationContext(), "标题" +
						// entry.getKey(), Toast.LENGTH_LONG).show();
						String value = entry.getKey();
						if (!TextUtils.isEmpty(value)) {
							smartAppend(value);
						}
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
							// Toast.makeText(getApplicationContext(), "(" + ci
							// + "," + cj + ")" + value,
							// Toast.LENGTH_LONG).show();
							if (!TextUtils.isEmpty(value)) {
								smartAppend(value);
							}
						}
					});

					row.addView(tv, new TableRow.LayoutParams(-2, -1));
				}

				TableLayout.LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
				table.addView(row, params);
			}
		}

		LinearLayout.LayoutParams tableParams = new LayoutParams(-2, -2);
		tableParams.setMargins(0, 0, 0, UnitConverter.dip2px(getApplicationContext(), 10));

		TextView title = new TextView(getApplicationContext());
		title.setText(String.format(getString(R.string.result_sql), history.get(history.size() - 1)));
		title.setTextColor(0xff000000);
		table.setTag(title);
		table_parent.addView(title);
		table_parent.addView(table, tableParams);
	}

	private void refreshUpdateResult() {
		// table.removeAllViews();
		// if (rows != null) {
		// rows.clear();
		// }
		status.setText(updateResult);
	}

	private void showHistory() {
		if (history == null || history.isEmpty()) {
			Toast.makeText(getApplicationContext(), R.string.no_history, Toast.LENGTH_SHORT).show();
			return;
		}
		if (historyWin != null) {
			if (historyWin.isShowing())
				historyWin.dismiss();
		}
		LayoutInflater inflater = LayoutInflater.from(this);
		historyWin = new QuickAction(text);

		historyWin.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				text.setSelected(false);
			}
		});

		for (String hist : history) {
			View view = inflater.inflate(R.layout.popup_item, null);
			final TextView textView = (TextView) view.findViewById(R.id.item);
			textView.setText(hist);
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					historyWin.dismiss();
					cmdLine.setText(textView.getText());
					showSoftImm();
				}
			});
			historyWin.addItem(view);
		}
		historyWin.show();
	}

	private void showCommonCmds() {
		if (commonCmds == null) {
			// load the hint here.
			commonCmds = new ArrayList<String>();
			commonCmds.addAll(Arrays.asList(getResources().getStringArray(R.array.common_used_cmd)));
		}
		if (commonCmdWin != null) {
			if (commonCmdWin.isShowing())
				return;
		} else {
			LayoutInflater inflater = LayoutInflater.from(this);
			commonCmdWin = new QuickAction(cmdLine);
			commonCmdWin.setTitle(R.string.common_cmds_title);

			for (String commonUsed : commonCmds) {
				View view = inflater.inflate(R.layout.popup_item, null);
				final TextView textView = (TextView) view.findViewById(R.id.item);
				textView.setText(commonUsed);
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						commonCmdWin.dismiss();
						String text = textView.getText().toString();
						cmdLine.setText(textView.getText());
						int index = text.indexOf("...");
						if (index != -1) {
							int end = index + 3;
							cmdLine.setSelection(index, end);
						}
						showSoftImm();
					}
				});
				commonCmdWin.addItem(view);
			}

		}
		commonCmdWin.show();
	}

	private void hideSoftImm() {
		imm.hideSoftInputFromWindow(cmdLine.getWindowToken(), 0);
	}

	private void showSoftImm() {
//		cmdLine.setFocusableInTouchMode(true);
//		cmdLine.requestFocus();
//		imm.showSoftInput(cmdLine,  0);
	    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  
	}

	@Override
	public void onClick(View view) {
		try {
			TextView tv = (TextView) view;
			smartAppend(tv.getText());
		} catch (Exception e) {

		}
	}
	
	private void smartAppend(CharSequence text){
		int selStart = cmdLine.getSelectionStart();
		int selEnd = cmdLine.getSelectionEnd();
		if( selEnd - selStart  == 0){
			cmdLine.append(" ");
			cmdLine.append(text);
			cmdLine.append(" ");
		}else{
			Editable cmd = cmdLine.getText();
			cmd.replace(selStart, selEnd, text);	
			cmdLine.setSelection(cmdLine.getSelectionEnd());
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
