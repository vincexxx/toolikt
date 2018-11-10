package com.vince.toolkit.uid;


import com.vince.toolkit.framework.util.spring.SpringContextUtil;
import com.vince.toolkit.uid.service.UidGeneratorService;

public class UidGenerator {

    private static UidGeneratorService uidGeneratorService = SpringContextUtil.getBean(UidGeneratorService.class);

    public static long getUID() {
        return uidGeneratorService.getUID();
    }

    public static String parseUID(long uid) {
        return uidGeneratorService.parseUID(uid);
    }

}
