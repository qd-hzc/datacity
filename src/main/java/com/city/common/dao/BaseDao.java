package com.city.common.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import com.city.common.pojo.Page;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseDao<T> implements IBaseDao<T> {
    @Autowired
    private SessionFactory sessionFactory;
    /**
     * 泛型类型
     */
    private Class entityType;
    /**
     * dao层日志
     */
    private Logger log;

    /**
     * 子类继承初始化需调用这个构造函数，给子类泛型确定类型
     */
    public BaseDao() {
        //获取dao层log对象
        log = LogManager.getLogger(this.getClass());
        //获取子类的泛型
        Type type = this.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = null;
            pType = (ParameterizedType) type;
            Type[] types = pType.getActualTypeArguments();
            if (types != null && types.length > 0) {
                entityType = (Class) types[0];
            } else {
                entityType = Object.class;
            }
        } else
            entityType = Object.class;
    }

    /**
     * 获取session
     *
     * @return Session
     */
    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * 保存数据并返回数据
     *
     * @param obj     要保存的数据
     * @param batched 是否批量提交
     * @return data
     */
    @Override
    public void insert(T obj, boolean batched) {
        getSession().save(obj);
        if (!batched)
            getSession().flush();
    }

    /**
     * 保存或修改数据
     *
     * @param obj     要保存或修改的数据
     * @param batched 是否批量提交
     */
    @Override
    public void saveOrUpdate(T obj, boolean batched) {
        getSession().saveOrUpdate(obj);
        if (!batched)
            getSession().flush();
    }

    /**
     * 删除数据
     *
     * @param obj     删除的数据
     * @param batched 是否批量提交
     */
    @Override
    public void delete(T obj, boolean batched) {
        getSession().delete(obj);
        if (!batched)
            getSession().flush();
    }

    /**
     * 修改数据
     *
     * @param obj     要修改的数据
     * @param batched 是否批量提交
     */
    @Override
    public void update(T obj, boolean batched) {
        getSession().update(obj);
        if (!batched)
            getSession().flush();
    }

    /**
     * 用HQL语句更新数据
     *
     * @param hql hql语句
     */
    @Override
    public int updateByHQL(String hql) {
        int result = 0;
        Session session = getSession();
        Query q = session.createQuery(hql);
        result = q.executeUpdate();
        session.flush();
        return result;
    }

    /**
     * 用HQL语句更新数据
     *
     * @param params 参数
     * @param hql    hql语句
     */
    public int updateWithParamsByHQL(String hql, Object[] params) {
        int result = 0;
        Session session = getSession();
        Query q = session.createQuery(hql);
        setParamsForQuery(q, params);
        result = q.executeUpdate();
        session.flush();
        return result;
    }

    /**
     * 根据id获取
     *
     * @param id
     */
    @Override
    public T queryById(Serializable id) {
        return (T) getSession().get(getType(), id);
    }

    /**
     * 根据id获取代理实体
     *
     * @param id
     */
    @Override
    public T loadById(Serializable id) {
        return (T) getSession().load(getType(), id);
    }

    /**
     * 根据hql语句查询，返回一个对象集合
     *
     * @param hql hql语句
     * @return List 结果集
     */
    @Override
    public List<T> queryByHQL(String hql) {
        Session s = getSession();
        Query q = getSession().createQuery(hql);
        return q.list();
    }

    /**
     * 根据sql语句查询
     */
    @Override
    public List queryBySql(String sql, Class clazz) {
        SQLQuery sqlQuery = getSession().createSQLQuery(sql);
        if (clazz != null) {
            sqlQuery.addEntity(clazz);
        }
        return sqlQuery.list();
    }

    /**
     * 根据sql语句查询
     */
    public List queryBySql(String sql) {
        return queryBySql(sql, null);
    }

    /**
     * 根据hql语句查询，返回一条数据
     *
     * @param hql sql语句
     * @return 一条结果
     */
    public T unqueryByHQL(String hql) {
        Query query = getSession().createQuery(hql);
        return (T) query.uniqueResult();
    }

    /**
     * 根据hql语句和条件查询
     *
     * @param hql    hql语句
     * @param params 条件
     * @return List 结果集
     */
    protected List<T> queryWithParamsByHQL(String hql, Object[] params) {
        Query q = getSession().createQuery(hql);
        setParamsForQuery(q, params);
        return q.list();
    }

    /**
     * 根据hql语句和分页条件查询
     *
     * @param hql  hql语句
     * @param page 分页条件
     * @return List 结果集
     */
    protected List<T> queryWithPageByHQL(String hql, Page page) {
        Query q = getSession().createQuery(hql);
        setPageParamsForQuery(q, page);
        return q.list();
    }

    /**
     * 根据hql语句,查询条件和分页条件查询
     *
     * @param hql    hql语句
     * @param params 查询参数
     * @param page   分页条件
     * @return List 结果集
     */
    protected List<T> queryWithParamsAndPageByHQL(String hql, Object[] params, Page page) {
        Query q = getSession().createQuery(hql);
        setParamsForQuery(q, params);
        setPageParamsForQuery(q, page);
        return q.list();
    }

    /**
     * 给Query添加条件
     */
    protected void setParamsForQuery(Query q, Object[] params) {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                q.setParameter(i, params[i]);
            }
        }
    }

    /**
     * 给Query添加分页条件
     */
    public void setPageParamsForQuery(Query q, Page page) {
        if (page != null) {
            q.setMaxResults(page.getLimit());
            q.setFirstResult(page.getStart());
        }
    }

    /**
     * 查询全部结果
     *
     * @return List 结果集
     */
    @Override
    public List<T> queryAll() {
        List<T> result = null;
        String hql = "from " + getType().getSimpleName();
        result = queryByHQL(hql);
        return result;
    }

    public void flush() {
        getSession().flush();
    }

    public void clearSession() {
        getSession().clear();
    }

    /**
     * 获取当前Dao操作的实体类型
     */
    private Class getType() {
        return entityType;

    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

}
