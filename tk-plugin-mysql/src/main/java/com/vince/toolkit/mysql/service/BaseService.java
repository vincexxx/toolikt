package com.vince.toolkit.mysql.service;

import com.vince.toolkit.base.exception.BusinessException;
import com.vince.toolkit.base.model.BaseModel;
import com.vince.toolkit.mysql.dao.IBaseDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BaseService implements IBaseService {
    @Autowired
    private IBaseDAO baseDAO;

    @Override
    public <E, T> List<E> find(Class<T> clazz, Object params) {
        return find(clazz.getName() + ".list", params);
    }

    @Override
    public <E> List<E> find(String statement, Object params) {
        return baseDAO.find(statement, params);
    }

    @Override
    public <T> int count(Class<T> clazz, Object params) {
        return count(clazz.getName() + ".count", params);
    }

    @Override
    public int count(String statement, Object params) {
        return baseDAO.count(statement, params);
    }


    @Override
    public <T> T read(Class<T> clazz, Object params) {
        return baseDAO.read(clazz, params);
    }

    @Override
    public <T> T read(String statement, Object params) {
        return baseDAO.read(statement, params);
    }

    @Override
    public int insert(BaseModel model) {
        if (model.getId() == null) {
            throw new BusinessException("保存数据库对象id不能为空");
        }
        return baseDAO.insert(model);
    }

    @Override
    public int insert(String statement, BaseModel model) {
        if (model.getId() == null) {
            throw new BusinessException("保存数据库对象id不能为空");
        }
        return baseDAO.insert(statement, model);
    }

    @Override
    public int insertBatch(List<? extends BaseModel> modelList) {
        for (BaseModel model : modelList) {
            if (model.getId() == null) {
                throw new BusinessException("保存数据库对象id不能为空");
            }
        }
        return baseDAO.insertBatch(modelList);
    }

    @Override
    public int insertBatch(String statement, List<? extends BaseModel> modelList) {
        for (BaseModel model : modelList) {
            if (model.getId() == null) {
                throw new BusinessException("保存数据库对象id不能为空");
            }
        }
        return baseDAO.insertBatch(statement, modelList);
    }

    @Override
    public int update(BaseModel model) {
        return baseDAO.update(model);
    }

    @Override
    public int update(String statement, Object params) {
        return baseDAO.update(statement, params);
    }

    @Override
    public int delete(BaseModel model) {
        return baseDAO.delete(model);
    }

    @Override
    public int delete(String statement, Object params) {
        return baseDAO.delete(statement, params);
    }

}
