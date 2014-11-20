package com.piglet.dao.pojo;

import android.text.TextUtils;

public class TableProperty {
	private String field;
	private String type;
	private String _null;
	private String key;
	private String _default;
	private String extra;
	
	public TableProperty(){
		
	}
	
	public TableProperty(TableProperty prop){
		this.field = prop.field;
		this.type = prop.type;
		this._null = prop._null;
		this.key = prop.key;
		this._default = prop._default;
		this.extra = prop.extra;
	}
	
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String get_null() {
		return _null;
	}
	public void set_null(String _null) {
		this._null = _null;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String get_default() {
		return _default;
	}
	public void set_default(String _default) {
		this._default = _default;
	}
	public String getExtra() {
		return extra;
	}
	public void setExtra(String extra) {
		this.extra = extra;
	}
	
	public String getName(){
		return field;
	}
	
	public String getContent(){
		StringBuilder sb = new StringBuilder();
		sb.append(type);
		if(!TextUtils.isEmpty(key)){
			sb.append(", ").append(key);
		}
		if(_null.equals("YES")){
			sb.append(", ").append("can be null");
		}else{
			sb.append(", ").append("not null");
		}
		if(!TextUtils.isEmpty(_default)){
			sb.append(", ").append("default for ").append(_default);
		}
		if(!TextUtils.isEmpty(extra)){
			sb.append(", ").append(extra);
		}
		return sb.toString();
	}
	
	
}
