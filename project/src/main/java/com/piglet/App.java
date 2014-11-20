package com.piglet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.BadSqlGrammarException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.piglet.core.MysqlConnection;
import com.piglet.dao.pojo.ConnectionInfo;
import com.piglet.dao.pojo.Database;
import com.piglet.dao.pojo.Table;
import com.piglet.dao.pojo.TableProperty;
import com.piglet.utils.Constant;
import com.piglet.worker.Worker;


/**
 * Hello world!
 *
 */
public class App 
{
	private static App instance = new App();
	
	private MysqlConnection connection;
	
	private App(){

	}
	
	public static App get(){
		return instance;
	}
	
	public MysqlConnection connect(ConnectionInfo info){
		if(connection !=null){
			connection.close();
		}
		connection = new MysqlConnection(info);
		return connection;
	}
	
	public MysqlConnection getConnection(){
		return connection;
	}
	
	public void close(){
		if(connection != null){
			connection.close();
			connection = null;
		}
	}
	
	
    public void process()
    {
//    	ConnectionInfo info = new ConnectionInfo("test", "10.203.7.202",3306,"information_schema","root","ss1013");
//    	MysqlConnection dao =new MysqlConnection(info); 
//		dao.showDbs();
//		dao.showTables();
//		dao.queryAll("apn_user");
//		dao.useDb("mysql");
//		dao.showTables();
//		//app.dao.queryAll("user");
//		dao.useDb("hammer");
//		dao.showTables();
//		dao.useDb("apn");
//		dao.showTables();
    }

    public void showDbs(final Handler handler){
		Runnable task = new Runnable(){
			public void run(){
				try{
					List<Database> list = connection.showDbs();	
					Message msg = Message.obtain();
					if(list == null){
						handler.sendEmptyMessage(Constant.value.WHAT_DATA_EMPTY);
					}else{
						msg.what = Constant.value.WHAT_DATA_RETURNED;
						msg.obj = list;
						handler.sendMessage(msg);	
					}	
				}catch(Exception e){
					e.printStackTrace();
					handler.sendEmptyMessage(Constant.value.WHAT_FAILED_TO_CONNECT);
				}
			}
		};
		Worker.getInstance().addTask(task);
    }
    
    public void selectDb(final String dbname, final Handler handler){
		Runnable task = new Runnable(){
			public void run(){
				try{
					connection.useDb(dbname);	
					List<Table> list = connection.showTables();
					Message msg = Message.obtain();
					if(list == null){
						handler.sendEmptyMessage(Constant.value.WHAT_DATA_EMPTY);
					}else{
						msg.what = Constant.value.WHAT_DATA_RETURNED;
						msg.obj = list;
						handler.sendMessage(msg);	
					}
				}catch(Exception e){
					e.printStackTrace();
					handler.sendEmptyMessage(Constant.value.WHAT_FAILED_TO_CONNECT);
				}
			}
		};
		Worker.getInstance().addTask(task);
    }
    
    public void descTable(final String tablename, final Handler handler){
		Runnable task = new Runnable(){
			public void run(){
				try{
					List<TableProperty> list = connection.schema(tablename);
					Message msg = Message.obtain();
					if(list == null){
						handler.sendEmptyMessage(Constant.value.WHAT_DATA_EMPTY);
					}else{
						msg.what = Constant.value.WHAT_DATA_RETURNED;
						msg.obj = list;
						handler.sendMessage(msg);	
					}
				}catch(Exception e){
					e.printStackTrace();
					handler.sendEmptyMessage(Constant.value.WHAT_FAILED_TO_CONNECT);
				}
			}
		};
		Worker.getInstance().addTask(task);
    }
    
    public void queryTable(final String tablename, final int page, final int pagesize, final Handler handler){
		Runnable task = new Runnable(){
			public void run(){
				try{
					List<Map<String , Object>> list = connection.queryAll(tablename, page, pagesize);
					Message msg = Message.obtain();
					if(list == null){
						handler.sendEmptyMessage(Constant.value.WHAT_DATA_EMPTY);
					}else{
						msg.what = Constant.value.WHAT_DATA_RETURNED;
						msg.obj = list;
						msg.arg1 = connection.queryItemCount(tablename);
						handler.sendMessage(msg);	
					}
				}catch(Exception e){
					e.printStackTrace();
					handler.sendEmptyMessage(Constant.value.WHAT_FAILED_TO_CONNECT);
				}
			}
		};
		Worker.getInstance().addTask(task);
    }
    
    public void dosql(final String sql, final Handler handler){
		Runnable task = new Runnable(){
			public void run(){
				try{
					HashMap<String , Object> result = connection.dosql(sql);
					int type = Integer.parseInt(result.get(Constant.key.QUERY_TYPE).toString());
					Message msg = Message.obtain();
					switch(type){
					case 0:// query
						List<Map<String,Object>> list = (List<Map<String, Object>>) result.get(Constant.key.QUERY_RESULT);
						msg.what = Constant.value.WHAT_DATA_RETURNED;
						msg.obj = list;
						msg.arg1 = list.size();
						break;
					case 1: //update
						msg.what = Constant.value.WHAT_UPDATE_RETURNED;
						msg.arg1 = Integer.parseInt(result.get(Constant.key.QUERY_RESULT).toString());
						break;
					}
					Bundle b = new Bundle();
					b.putFloat(Constant.key.QUERY_TIME, (Float)result.get(Constant.key.QUERY_TIME));
					msg.setData(b);
					handler.sendMessage(msg);
				}catch(BadSqlGrammarException e){
					Message msg = Message.obtain();
					msg.what = Constant.value.WHAT_ERROR_OCCURRED;
					msg.obj = e.getSQLException().getMessage();
					handler.sendMessage(msg);
				}catch(Exception e){
					e.printStackTrace();
					handler.sendEmptyMessage(Constant.value.WHAT_FAILED_TO_CONNECT);
				}
			}
		};
		Worker.getInstance().addTask(task);
    }
    
}
