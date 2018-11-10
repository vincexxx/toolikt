package com.vince.toolkit.mysql.dao;

import com.vince.toolkit.base.model.BaseModel;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class BaseDAO implements IBaseDAO {
    @Resource
    private SqlSession sqlSession;

    @Override
    public <E, T> List<E> find(Class<T> clazz, Object params) {
        return find(clazz.getName() + ".list", params);
    }

    @Override
    public <E> List<E> find(String statement, Object params) {
        return sqlSession.selectList(statement, params);
    }

    @Override
    public <T> int count(Class<T> clazz, Object params) {
        return count(clazz.getName() + ".count", params);
    }

    @Override
    public int count(String statement, Object params) {
        return sqlSession.selectOne(statement, params);
    }

    @Override
    public <T> T read(Class<T> clazz, Object params) {
        return read(clazz.getName() + ".read", params);
    }

    @Override
    public <T> T read(String statement, Object params) {
        return sqlSession.<T>selectOne(statement, params);
    }

    @Override
    public <T> int insert(T model) {
        return insert(model.getClass().getName() + ".insert", model);
    }

    @Override
    public <T> int insertBatch(List<T> modelList) {
        return insertBatch(modelList.get(0).getClass().getName() + ".insertBatch", modelList);
    }

    @Override
    public <T> int insert(String statement, T model) {
        return sqlSession.insert(statement, model);
    }

    @Override
    public <T> int insertBatch(String statement, List<T> modelList) {
        return insert(statement, modelList);
    }

    @Override
    public int update(BaseModel model) {
        return update(model.getClass().getName() + ".update", model);
    }

    @Override
    public int update(String statement, Object params) {
        return sqlSession.update(statement, params);
    }

    @Override
    public int delete(BaseModel model) {
        return delete(model.getClass().getName() + ".delete", model.getId());
    }

    @Override
    public int delete(String statement, Object params) {
        return sqlSession.delete(statement, params);
    }

    public SqlSession getSqlSession() {
        return sqlSession;
    }

}
