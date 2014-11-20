package com.ben.data;

import java.util.List;

public interface GenericDao<T> {
	
	public void save(T entity);
	
	public void update(T entity);

	public void delete(T entity);

	public T findById(Object id);

	//所有非单记录查询，都应该提供相应的分页接口。
	public List<T> findAll();

	public List<T> findAll(int pageMax, int pageStart);

	public List<T> findByKeys(String[] keys, Object[] values);
	
	public List<T> findByKeys(String[] keys, Object[] values, int pageMax, int pageStart);
}
