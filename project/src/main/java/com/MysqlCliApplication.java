package com;

import android.app.Application;

import com.ben.data.DatabaseHelper;

public class MysqlCliApplication extends Application{

	@Override
	public void onCreate() {
		DatabaseHelper.getInstance().init(this);
		super.onCreate();
	}
}
