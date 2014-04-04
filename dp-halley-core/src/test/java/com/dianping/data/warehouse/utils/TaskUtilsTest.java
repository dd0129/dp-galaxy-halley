package com.dianping.data.warehouse.utils;

import org.junit.Test;

import java.util.Date;

/**
 * Created by hongdi.tang on 14-4-4.
 */
public class TaskUtilsTest {

    @Test
    public void testGenerateInstanceID() throws Exception {
        Date d = new Date();
        System.out.println(TaskUtils.generateInstanceID(10001,"D",d));
        System.out.println(TaskUtils.generateInstanceID(10001,"M",d));
        System.out.println(TaskUtils.generateInstanceID(10001,"W",d));
        System.out.println(TaskUtils.generateInstanceID(10001,"H",d));
        System.out.println(TaskUtils.generateInstanceID(10001,"mi",d));
    }

    @Test
    public void testGenerateRelaInstanceID() throws Exception {
        Long fireTime = System.currentTimeMillis();
        System.out.println(TaskUtils.generateRelaInstanceID(10002,fireTime,"D0"));
        System.out.println(TaskUtils.generateRelaInstanceID(10002,fireTime,"W1"));
        System.out.println(TaskUtils.generateRelaInstanceID(10002,fireTime,"M0"));
        System.out.println(TaskUtils.generateRelaInstanceID(10002,fireTime,"H0"));
    }
}
