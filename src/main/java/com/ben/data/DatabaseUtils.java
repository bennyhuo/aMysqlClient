package com.ben.data;

import net.tsz.afinal.FinalDb;

public class DatabaseUtils {
	public static FinalDb getDatabase(){
		return DatabaseHelper.getInstance().getDb();
	}
}
