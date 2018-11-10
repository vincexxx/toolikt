package com.vince.toolkit.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface JedisClient {

    public Long del(String key);

    public String get(String key, Integer expireSecond);

    public String set(String key, String value, Integer expireSecond);

    public String set(String key, byte[] value, Integer expireSecond);

    public Long expire(String key, Integer expireSecond);

    public boolean exists(String key);

    public Long delHashFields(String key, String... field);

    public String getHashField(String key, String field, Integer expireSecond);

    public List<String> getHashFields(String key, Integer expireSecond, String... fields);

    public Set<String> getHashAllFieldKeys(String key, Integer expireSecond);

    public Map<String, String> getHashAll(String key, Integer expireSecond);

    public Long setHashField(String key, String fieldKey, String value, Integer expireSecond);

    public String setHash(String key, Map<String, String> value, Integer expireSecond);

    public Long incr(String key);

    public Long decr(String key);

    public Long ttl(String key);

}