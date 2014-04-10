package com.dianping.data.warehouse.resource;

import com.dianping.data.warehouse.domain.InstanceDO;
import com.dianping.data.warehouse.executer.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hongdi.tang on 14-3-15.
 */
public class ResourceManager2 {
    private ResourceManager2(){}
    private static Logger logger = LoggerFactory.getLogger(ResourceManager2.class);
    private static final String RESOURCE_FILE = "conf/resource.properties";

    @Resource(name="task")
    private Task task;

    @Bean
    public static ThreadPoolTaskExecutor taskExecutor(Integer maxSize){
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setMaxPoolSize(maxSize);
        //pool.setWaitForTasksToCompleteOnShutdown(true);
        return pool;
    }

    private static Map<String,Executor> RESOURCE_MAP = new ConcurrentHashMap<String, Executor>();

    static{
        Properties props = new Properties();
        try{
            props.load(ResourceManager2.class.getClassLoader().getResourceAsStream(RESOURCE_FILE));
            for (String key : props.stringPropertyNames()){
                Executor threadPool = Executors.newFixedThreadPool(Integer.valueOf(props.getProperty(key)));
                RESOURCE_MAP.put(key,threadPool);
            }
        }catch(Exception e){
            logger.error("load resource error",e);
            throw new Error("load resource error");
        }
    }

    public static void submit(InstanceDO inst){
        Executor threadPool = RESOURCE_MAP.get(inst.getDatabaseSrc());
        synchronized (threadPool){
            if(threadPool == null){
                RESOURCE_MAP.put(inst.getDatabaseSrc(), Executors.newFixedThreadPool(1));
                logger.info(inst.getDatabaseSrc().concat(" join Resouce Map"));
                threadPool.execute(new Task());
            }else{
                threadPool.execute(new Task());
            }
        }
    }

}
