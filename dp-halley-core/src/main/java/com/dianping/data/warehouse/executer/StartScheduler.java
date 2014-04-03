package com.dianping.data.warehouse.executer;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by adima on 14-3-31.
 */
public class StartScheduler {
    public static void main(String[] args){
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring-applicationcontext.xml");
        context.getBean("fuck");


    }
}
