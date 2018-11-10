package com.vince.toolkit.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class JedisClientCluster implements JedisClient {

    @Autowired
    private JedisCluster jedisCluster;

    /**
     * 删除key
     *
     * @param key
     * @return
     */
    public Long del(String key) {
        return jedisCluster.del(key);
    }

    /**
     * 获取数据并设置失效时间，传null不设置
     *
     * @param key
     * @param expireSecond
     * @return
     */
    public String get(String key, Integer expireSecond) {
        String value = jedisCluster.get(key);
        if (expireSecond != null) {
            jedisCluster.expire(key, expireSecond);
        }
        return value;
    }

    /**
     * 存储String对象
     * 并设置失效时间，传null不设置
     *
     * @param key
     * @param value
     * @param expireSecond
     * @return
     */
    public String set(String key, String value, Integer expireSecond) {
        String result = jedisCluster.set(key, value);
        if (expireSecond != null) {
            jedisCluster.expire(key, expireSecond);
        }
        return result;
    }

    /**
     * 存储byte数组
     * 并设置失效时间，传null不设置
     *
     * @param key
     * @param value
     * @param expireSecond
     * @return
     */
    public String set(String key, byte[] value, Integer expireSecond) {
        String result = jedisCluster.set(key.getBytes(), value);
        if (expireSecond != null) {
            jedisCluster.expire(key, expireSecond);
        }
        return result;
    }

    /**
     * 设置redis key时效时间
     *
     * @param key
     * @param expireSecond
     * @return
     */
    public Long expire(String key, Integer expireSecond) {
        return jedisCluster.expire(key, expireSecond);
    }

    /**
     * 判断key是否存在
     *
     * @param key
     * @return
     */
    public boolean exists(String key) {
        return jedisCluster.exists(key);
    }

    /**
     * 删除指定的多个hash field
     *
     * @param key
     * @param field
     * @return
     */
    public Long delHashFields(String key, String... field) {
        return jedisCluster.hdel(key, field);
    }

    /**
     * 获取指定的hash field数据
     *
     * @param key
     * @param field
     * @param expireSecond
     * @return
     */
    public String getHashField(String key, String field, Integer expireSecond) {
        String result = jedisCluster.hget(key, field);
        if (expireSecond != null) {
            jedisCluster.expire(key, expireSecond);
        }
        return result;
    }

    /**
     * 获取多个指定的hash field数据
     *
     * @param key
     * @param expireSecond
     * @param fields
     * @return
     */
    public List<String> getHashFields(String key, Integer expireSecond, String... fields) {
        List<String> result = jedisCluster.hmget(key, fields);
        if (expireSecond != null) {
            jedisCluster.expire(key, expireSecond);
        }
        return result;
    }

    /**
     * 获取hash的所有field key
     *
     * @param key
     * @param expireSecond
     * @return
     */
    public Set<String> getHashAllFieldKeys(String key, Integer expireSecond) {
        Set<String> result = jedisCluster.hkeys(key);
        if (expireSecond != null) {
            jedisCluster.expire(key, expireSecond);
        }
        return result;
    }

    public Map<String, String> getHashAll(String key, Integer expireSecond) {
        Map<String, String> result = jedisCluster.hgetAll(key);
        if (expireSecond != null) {
            jedisCluster.expire(key, expireSecond);
        }
        return result;
    }

    /**
     * 保存hash field数据
     *
     * @param key
     * @param fieldKey
     * @param value
     * @param expireSecond
     * @return
     */
    public Long setHashField(String key, String fieldKey, String value, Integer expireSecond) {
        Long result = jedisCluster.hset(key, fieldKey, value);
        if (expireSecond != null) {
            jedisCluster.expire(key, expireSecond);
        }
        return result;
    }

    public String setHash(String key, Map<String, String> value, Integer expireSecond) {
        String result = jedisCluster.hmset(key, value);
        if (expireSecond != null) {
            jedisCluster.expire(key, expireSecond);
        }
        return result;
    }

    public Long incr(String key) {
        return jedisCluster.incr(key);
    }

    public Long decr(String key) {
        return jedisCluster.decr(key);
    }

    public Long ttl(String key) {
        return jedisCluster.ttl(key);
    }


}