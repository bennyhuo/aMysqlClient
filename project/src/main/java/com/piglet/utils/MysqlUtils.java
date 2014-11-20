package com.piglet.utils;

import android.text.TextUtils;

public class MysqlUtils {
	public static boolean checkHost(String host) {
		if (TextUtils.isEmpty(host) || !host.matches("[a-zA-z\\d.-]+")) {
			return false;
		}
		return true;
	}

	public static boolean checkDbName(String dbname) {
		if (TextUtils.isEmpty(dbname) || !dbname.matches("[a-zA-z\\d_]+")) {
			return false;
		}
		return true;
	}

	public static boolean checkUserName(String username) {
		if (TextUtils.isEmpty(username)) {
			return false;
		}
		return true;
	}

	public static boolean checkPassword(String password) {
		if (TextUtils.isEmpty(password)) {
			return false;
		}
		return true;
	}
}
