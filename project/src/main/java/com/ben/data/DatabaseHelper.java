package com.ben.data;

import net.tsz.afinal.FinalDb;
import android.content.Context;

/**
 * 绑定到主线程了
 * @author Enbandari
 *
 */
public class DatabaseHelper{
	private static DatabaseHelper instance;
	private Context context;
	private FinalDb db;
	private ThreadLocal<FinalDb> dbHolder;
	
	private DatabaseHelper() {

	}
	
	public void init(Context context){
		this.context = context;
		dbHolder = new ThreadLocal<FinalDb>();
		db = FinalDb.create(context);
		dbHolder.set(db);
	}
	
	public static DatabaseHelper getInstance(){
		if(instance == null){
			instance = new DatabaseHelper();
		}
		return instance;
	}
	
	public FinalDb getDb(){
		return db;
	}
}
