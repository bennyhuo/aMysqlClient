package com.piglet.dao.pojo;

import net.tsz.afinal.annotation.sqlite.Id;

public class Table {
	@Id
	private String name;
	
	private String content;

	public Table(){
		
	}
	
	public Table(Table table){
		this.name = table.name;
		this.content = table.content;
	}
	
	public Table(String name , String content){
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
