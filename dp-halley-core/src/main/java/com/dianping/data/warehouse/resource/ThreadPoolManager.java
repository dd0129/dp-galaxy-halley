package com.dianping.data.warehouse.resource;

import com.dianping.data.warehouse.domain.InstanceDO;
import com.dianping.data.warehouse.executer.Task;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by adima on 14-3-24.
 */
public class ThreadPoolManager {
    private static final Integer MAX_SIZE = 200;
    private static ThreadPoolTaskExecutor pool;
    private ThreadPoolManager(){};

    static {
        pool = new ThreadPoolTaskExecutor();
        pool.setMaxPoolSize(MAX_SIZE);
        pool.initialize();
    }

    public static void registerTask(Task task){
        pool.execute(task);
    }



}
