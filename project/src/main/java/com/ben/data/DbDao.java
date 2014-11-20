package com.ben.data;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class DbDao<T> implements GenericDao<T> {
	private Class<T> clazz;
	
	@SuppressWarnings("unchecked")
	public DbDao(){
		clazz = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	@Override
	public void save(T entity) {
		DatabaseUtils.getDatabase().save(entity);
	}

	@Override
	public void update(T entity) {
		DatabaseUtils.getDatabase().update(entity);		
	}

	@Override
	public void delete(T entity){
		DatabaseUtils.getDatabase().delete(entity);
	}	

	@Override
	public T findById(Object id) {
		return DatabaseUtils.getDatabase().findById(id, clazz);
	}

	@Override
	public List<T> findAll() {
		return DatabaseUtils.getDatabase().findAll(clazz);
	}
	
	public int count(){
		return DatabaseUtils.getDatabase().countAll(clazz);
	}

	@Override
	public List<T> findAll(int pageMax, int pageStart) {
		return DatabaseUtils.getDatabase().findAll(clazz, pageMax,pageStart);
	}

	@Override
	public List<T> findByKeys(String[] keys, Object[] values) {
		return findByKeys(keys, values, 0, 0);
	}
	
	public int count(String[] keys, Object[] values){
		StringBuilder where = new StringBuilder();
		if(keys.length != values.length){
			throw new RuntimeException("keys and values doesn't match.");
		}
		int len = keys.length;
		for(int i = 0; i < len ; ++i){
			if(i!=0){
				where.append(" AND ");
			}
			where.append(keys[i]+"='"+values[i].toString()+"'");
		}
		return DatabaseUtils.getDatabase().countByWhere(clazz, where.toString());
	}

	@Override
	public List<T> findByKeys(String[] keys, Object[] values, int pageMax, int pageStart) {
		StringBuilder where = new StringBuilder();
		if(keys.length != values.length){
			throw new RuntimeException("keys and values doesn't match.");
		}
		int len = keys.length;
		for(int i = 0; i < len ; ++i){
			if(i!=0){
				where.append(" AND ");
			}
			where.append(keys[i]+"='"+values[i].toString()+"'");
		}
		
		return DatabaseUtils.getDatabase().findAllByWhere(clazz, where.toString(), pageMax, pageStart);
	}

}
