package com.vince.toolkit.framework.util.spring;

import com.vince.toolkit.framework.util.log.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringInitialize {

    private final static Logger logger = LoggerFactory.getLogger(SpringInitialize.class);

    private static ClassPathXmlApplicationContext context;

    public static void init() {
        LogUtil.info(logger, "Start init Spring...");
        context = new ClassPathXmlApplicationContext(new String[]{"classpath*:spring/*.xml"});
        new SpringContextUtil().setApplicationContext(context);
        context.start();
        LogUtil.info(logger, "Spring initialized...");
    }

}
