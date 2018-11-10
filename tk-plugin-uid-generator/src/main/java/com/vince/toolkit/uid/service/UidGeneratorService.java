package com.vince.toolkit.uid.service;


import com.vince.toolkit.base.exception.BusinessException;

public interface UidGeneratorService {

    long getUID() throws BusinessException;

    String parseUID(long uid);

}
