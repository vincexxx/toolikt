package com.vince.toolkit.framework.property;

import com.vince.toolkit.framework.util.log.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class MultiPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    private final Logger logger = LoggerFactory.getLogger(MultiPropertyPlaceholderConfigurer.class);

    //缓存所以配置
    private Properties properties;

    @Override
    protected Properties mergeProperties() throws IOException {
        Properties mergeProperties = super.mergeProperties();
        this.properties = mergeProperties;
        if (logger.isDebugEnabled()) {
            Set<Map.Entry<Object, Object>> entrySet = properties.entrySet();
            for (Map.Entry<Object, Object> entry : entrySet) {
                LogUtil.debug(logger, new Object[]{entry.getKey(), entry.getValue()});
            }
        }
        return mergeProperties;
    }

    /**
     * 获取配置
     */
    public String getString(String key) {
        return resolvePlaceholder(key, properties);
    }

    /**
     * 获取Integer型数据
     */
    public Integer getInteger(String key) {
        String str = resolvePlaceholder(key, properties);
        return str == null ? null : Integer.parseInt(str);
    }

}