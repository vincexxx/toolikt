package com.vince.toolkit.mysql.dao;

import com.vince.toolkit.base.model.BaseModel;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public interface IBaseDAO {

    /**
     * 通过模型类型与业务参数查模型列表
     */
    public <E, T> List<E> find(Class<T> clazz, Object params);

    /**
     * 通过statement与业务参数查模型列表
     */
    public <E> List<E> find(String statement, Object params);

    /**
     * 通过模型类型与业务参数查模型数量
     */
    public <T> int count(Class<T> clazz, Object params);

    /**
     * 通过statement与业务参数查模型数量
     */
    public int count(String statement, Object params);

    /**
     * 通过模型类型与主键查模型
     */
    public <T> T read(Class<T> clazz, Object params);

    /**
     * 通过statement与ID查询模型
     *
     * @return
     */
    public <T> T read(String statement, Object params);

    /**
     * 保存模型
     *
     * @param <T>
     * @return
     */
    public <T> int insert(T model);

    /**
     * 保存模型(批量)
     *
     * @param modelList
     * @param <T>
     * @return
     */
    public <T> int insertBatch(List<T> modelList);

    /**
     * 通过statement保存模型
     *
     * @return
     */
    public <T> int insert(String statement, T model);

    /**
     * 保存模型(批量)
     *
     * @param statement
     * @param modelList
     * @param <T>
     * @return
     */
    public <T> int insertBatch(String statement, List<T> modelList);

    /**
     * 更新模型
     *
     * @param model
     * @return
     */
    public int update(BaseModel model);

    /**
     * 通过statement更新模型
     *
     * @return
     */
    public int update(String statement, Object params);

    /**
     * 删除模型
     *
     * @return
     */
    public int delete(BaseModel model);

    /**
     * 通过statement与模型删除模型
     *
     * @return
     */
    public int delete(String statement, Object params);

    /**
     * 获取session
     *
     * @return
     */
    public SqlSession getSqlSession();

}
