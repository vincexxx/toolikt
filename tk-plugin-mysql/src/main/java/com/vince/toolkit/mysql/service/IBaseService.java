package com.vince.toolkit.mysql.service;


import com.vince.toolkit.base.model.BaseModel;

import java.util.List;

public interface IBaseService {
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
     * 通过主键读取模型
     *
     * @param id
     * @return
     */
    // public <T> T read(Object id);

    /**
     * 通过statement与业务参数查模型
     *
     * @return
     */
    public <T> T read(String statement, Object params);

    /**
     * 通过statement与主键读取模型
     *
     * @return
     */
    public <T> T read(Class<T> clazz, Object id);

    /**
     * 新增模型
     *
     * @param model
     * @return
     */
    public int insert(BaseModel model);

    /**
     * 新增模型(batch)
     *
     * @param modelList
     * @return
     */
    public int insertBatch(List<? extends BaseModel> modelList);

    /**
     * 新增模型
     *
     * @param model
     * @return
     */
    public int insert(String statement, BaseModel model);

    /**
     * @param statement
     * @param modelList
     * @return
     */
    public int insertBatch(String statement, List<? extends BaseModel> modelList);

    /**
     * 更新模型
     *
     * @param model
     * @return
     */
    public int update(BaseModel model);

    /**
     * 更新操作
     *
     * @param statement
     * @param params
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
     * 通过statement与模型删除
     *
     * @return
     */
    public int delete(String statement, Object params);

}
