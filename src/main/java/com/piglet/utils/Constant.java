package com.piglet.utils;

public class Constant {
	public class key {
		public static final String CONNECTION_INFO_NAME = "connection_info_name";
		public static final String CONNECTION_INFO = "connection_info";
		
		public static final String DATABASE = "Database";
		public static final String TABLE = "table";
		public static final String TABLE_IN_DB = "Tables_in_";
		
		public static final String QUERY_TIME = "query_time";
		public static final String QUERY_TYPE= "query_type";
		public static final String QUERY_RESULT = "query_result";
	}
	
	public class value{
		public static final int WHAT_DATA_RETURNED = 0;
		public static final  int WHAT_DATA_EMPTY = 1;
		public static  final int WHAT_FAILED_TO_CONNECT = 2;
		public static  final int WHAT_UPDATE_RETURNED= 3;
		public static final int WHAT_ERROR_OCCURRED = 4;
	}
}
