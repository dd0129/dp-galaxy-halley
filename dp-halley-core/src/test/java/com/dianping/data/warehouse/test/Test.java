package com.dianping.data.warehouse.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by adima on 14-4-10.
 */
public class Test {

    public static void main(String[] args){
        ApplicationContext appContext = new ClassPathXmlApplicationContext("classpath:test-spring-applicationcontext.xml");
        TaskExecutorExample te = (TaskExecutorExample)appContext.getBean("taskExecutorExample");
        te.printMessages();
    }
}
