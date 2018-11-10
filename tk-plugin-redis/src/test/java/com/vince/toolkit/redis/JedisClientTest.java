package com.vince.toolkit.redis;

import com.vince.toolkit.framework.util.log.LogInitialize;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Vince on 18/11/8.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/*context.xml"})
public class JedisClientTest {

    static {
        LogInitialize.init();
    }


    @Autowired
    private JedisClient jedisClient;


    @Test
    public void test() {
    }


}
