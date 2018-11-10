package com.vince.toolkit.redis.factory;

import com.vince.toolkit.base.exception.BusinessException;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

public class JedisClusterFactory implements FactoryBean<JedisCluster>, InitializingBean {

    private Resource addressConfig;
    private String addressKeyPrefix;

    private JedisCluster jedisCluster;
    private Integer timeout;
    private Integer maxRedirections;
    private GenericObjectPoolConfig genericObjectPoolConfig;

    private Pattern p = Pattern.compile("^.+[:]\\d{1,5}\\s*$");

    private JedisClusterFactory() {
    }

    @Override
    public JedisCluster getObject() throws Exception {
        return jedisCluster;
    }

    @Override
    public Class<? extends JedisCluster> getObjectType() {
        return (this.jedisCluster != null ? this.jedisCluster.getClass() : JedisCluster.class);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }


    private Set<HostAndPort> parseHostAndPort() throws BusinessException {
        try {
            Properties prop = new Properties();
            prop.load(this.addressConfig.getInputStream());

            Set<HostAndPort> haps = new HashSet<>();
            for (Object key : prop.keySet()) {
                if (!((String) key).startsWith(addressKeyPrefix)) {
                    continue;
                }
                String val = (String) prop.get(key);
                boolean isIpPort = p.matcher(val).matches();
                if (!isIpPort) {
                    throw new BusinessException("配置文件:key:[%s]对应的value:[%s]不合法", key, val);
                }
                String[] ipAndPort = val.split(":");
                HostAndPort hap = new HostAndPort(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
                haps.add(hap);
            }
            return haps;
        } catch (Exception ex) {
            throw new BusinessException("解析[redis]配置文件失败", ex);
        }
    }

    @Override
    public void afterPropertiesSet() throws BusinessException {
        Set<HostAndPort> haps = this.parseHostAndPort();
        jedisCluster = new JedisCluster(haps, timeout, maxRedirections, genericObjectPoolConfig);

    }

    public void setAddressConfig(Resource addressConfig) {
        this.addressConfig = addressConfig;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setMaxRedirections(int maxRedirections) {
        this.maxRedirections = maxRedirections;
    }

    public void setAddressKeyPrefix(String addressKeyPrefix) {
        this.addressKeyPrefix = addressKeyPrefix;
    }

    public void setGenericObjectPoolConfig(GenericObjectPoolConfig genericObjectPoolConfig) {
        this.genericObjectPoolConfig = genericObjectPoolConfig;
    }

} 