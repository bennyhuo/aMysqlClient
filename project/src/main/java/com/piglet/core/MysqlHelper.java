package com.piglet.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.piglet.R;

import android.content.Context;

public class MysqlHelper {
	// private static List<Map<String, Object>> helpList;
	private static Context context;

	public static void init(Context context) {
		MysqlHelper.context = context;
	}

	public static List<Map<String, Object>> getHelpList() {
		String[] keys = { "OP", "USAGE" };
		String[] ops = context.getResources().getStringArray(R.array.ops);
		String[] usages = context.getResources().getStringArray(R.array.usages);
		List<Map<String, Object>> helpList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < ops.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put(keys[0], ops[i]);
			map.put(keys[1], usages[i]);
			helpList.add(map);
		}
		return helpList;
	}

	public static List<Map<String, Object>> getAbout() {
		String[] keys = { "Title", "Content" };
		String[] ops = context.getResources().getStringArray(R.array.about_title);
		String[] usages = context.getResources().getStringArray(R.array.about_content);
		List<Map<String, Object>> helpList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < ops.length; i++) {
			LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
			map.put(keys[0], ops[i]);
			map.put(keys[1], usages[i]);
			helpList.add(map);
		}
		return helpList;
	}
}
