package com.city.common.dao;

import java.io.Serializable;
import java.util.List;

/**
 * dao层接口
 * 
 * @param <T>
 *            dao层操作的实体类
 */
public interface IBaseDao<T> {
	/**
	 * 插入一个实体
	 * 
	 * @param obj
	 *            要插入的实体
	 * @param batched
	 *            是否为批量
	 * @return
	 */
	public void insert(T obj, boolean batched);

	/**
	 * 插入或修改实体
	 * 
	 * @param obj
	 *            要操作的实体
	 * @param batched
	 *            是否为批量操作
	 */
	public void saveOrUpdate(T obj, boolean batched);

	/**
	 * 删除一个实体
	 * 
	 * @param obj
	 *            要删除的实体
	 * @param batched
	 *            是否为批量删除
	 */
	public void delete(T obj, boolean batched);

	/**
	 * 更新一个实体
	 * 
	 * @param obj
	 *            要更新的实体
	 * @param batched
	 *            是否为批量
	 */
	public void update(T obj, boolean batched);

	/**
	 * 根据id查询一个实体
	 * 
	 * @param id
	 * @return
	 */
	public T queryById(Serializable id);

	/**
	 * 查询全部实体
	 * 
	 * @return
	 */
	public List<T> queryAll();

	/**
	 * 根据hql语句查询
	 * 
	 * @param hql
	 * @return
	 */
	public List<T> queryByHQL(String hql);

	/**
	 * 根据sql语句查询
	 * @param sql
	 * @return
     */
	List queryBySql(String sql,Class clazz);

	/**
	 * 根据hql语句更新
	 * 
	 * @param hql
	 * @return
	 */
	public int updateByHQL(String hql);

	public T loadById(Serializable id);
}
