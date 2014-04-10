package com.dianping.data.warehouse.executer;

import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by adima on 14-4-9.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-spring-applicationcontext.xml")
public class ThreadTest {
   public static void main(String[] args ){
       ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
       ThreadPoolTaskExecutor taskExecutor =
               (ThreadPoolTaskExecutor)ctx.getBean("taskExecutor");

       TaskTest printTask1 = (TaskTest)ctx.getBean("taskTest");
//       printTask1.setInstanceDO(null);
//       taskExecutor.execute(printTask1);


    }
}
