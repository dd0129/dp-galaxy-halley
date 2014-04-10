package com.dianping.data.warehouse.executer;
  
import org.springframework.context.annotation.Bean;  
import org.springframework.context.annotation.ComponentScan;  
import org.springframework.context.annotation.Configuration;  
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;  
  
//@Configuration
//@ComponentScan(basePackages="com.dianping")
public class AppConfig {  
        //@Bean
        public ThreadPoolTaskExecutor taskExecutor(){
                ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();  
                pool.setMaxPoolSize(10);
                return pool;
        }  
}  