package com.vince.toolkit.uid;

import com.vince.toolkit.framework.util.log.LogInitialize;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Vince on 18/11/8.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/*context.xml"})
public class UidTest {

    private static final int SIZE = 10000000; // 10w

    static {
        LogInitialize.init();
    }


    @Test
    public void test() {
        long start = System.currentTimeMillis();
        Set<Long> uidSet = new ConcurrentSkipListSet<>();
        for (int i = 0; i < SIZE; i++) {
            long uid = UidGenerator.getUID();
            //uidSet.add(uid);
        }
        System.out.println("~~" + (System.currentTimeMillis() - start));
        System.out.println(uidSet.size());
    }

}
