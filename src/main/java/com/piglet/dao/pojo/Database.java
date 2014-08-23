package com.piglet.dao.pojo;

import net.tsz.afinal.annotation.sqlite.Id;

public class Database {
	@Id
	private String name;
	
	private String content;

	public Database(){
		
	}
	
	public Database(Database db){
		this.name = db.name;
		this.content = db.content;
	}
	
	public Database(String name , String content){
		this.name = name;
		this.content = content;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
