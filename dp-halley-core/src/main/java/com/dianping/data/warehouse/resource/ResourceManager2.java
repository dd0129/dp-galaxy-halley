package com.dianping.data.warehouse.resource;

import com.dianping.data.warehouse.domain.InstanceDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by adima on 14-3-24.
 */
public class ResourceManager2 {
    private static Map<String,Resource> RESOURCE_QUEUE = new ConcurrentHashMap<String, Resource>();

    private static Logger logger = LoggerFactory.getLogger(ResourceManager2.class);
    private static final String RESOURCE_FILE = "conf/resource.properties";

    private ResourceManager2(){};

    private static class Resource{
        private String resourceName;
        private Integer capability;
        private Map<String,InstanceDO> queue;

        public Resource(String resourceName, Integer capability,Map<String,InstanceDO> queue) {
            this.resourceName = resourceName;
            this.capability = capability;
            this.queue = queue;
        }

        public Integer getCapability() {
            return capability;
        }

        public void setCapability(Integer capability) {
            this.capability = capability;
        }

        public String getResourceName() {
            return resourceName;
        }

        public void setResourceName(String resourceName) {
            this.resourceName = resourceName;
        }

        public Map<String, InstanceDO> getQueue() {
            return queue;
        }

        public void setQueue(Map<String, InstanceDO> queue) {
            this.queue = queue;
        }
    }

    static{
        Properties props = new Properties();
        try{
            props.load(ResourceManager2.class.getClassLoader().getResourceAsStream(RESOURCE_FILE));
            for (String key : props.stringPropertyNames()){
                RESOURCE_QUEUE.put(key, new Resource(key, Integer.valueOf(props.getProperty(key)), new ConcurrentHashMap<String, InstanceDO>()));
            }
        }catch(Exception e){
            logger.error("load resource error",e);
            throw new Error("load resource error");
        }
    }

    private static synchronized boolean isDuplicateTask(InstanceDO inst){
        Map<String,InstanceDO> map = getAllInstance();
        for(InstanceDO value : map.values()){
            if(value.getTaskId().intValue() == inst.getTaskId().intValue()){
                return true;
            }
        }
        return false;
    }

    public static synchronized boolean idDupliateInstance(InstanceDO inst){
        Map<String,InstanceDO> map = getAllInstance();
        return map.containsKey(inst.getInstanceId());
    }

    public static synchronized int inQueue(InstanceDO inst){
        if(isDuplicateTask(inst)){
            logger.info(inst.getInstanceId() + "(" + inst.getTaskName() + ") exists duplicate task");
            return -1;
        }
        Resource resource = RESOURCE_QUEUE.get(inst.getDatabaseSrc());
        if(resource == null){
            resource = new Resource(inst.getDatabaseSrc(), 1,new ConcurrentHashMap<String,InstanceDO>());
            RESOURCE_QUEUE.put(inst.getDatabaseSrc(), resource);
            return 1;
        }else{
            if(resource.getQueue().size() < resource.capability){
                resource.queue.put(inst.getInstanceId(),inst);
                return size(inst.getDatabaseSrc());
            }else{
                logger.info(inst.getDatabaseSrc() + " is full capability :="+resource.getCapability());
                return -1;
            }
        }
    }

    public static synchronized int outQueue(InstanceDO inst){
        RESOURCE_QUEUE.get(inst.getDatabaseSrc()).queue.remove(inst.getInstanceId());
        return size(inst.getDatabaseSrc());
    }

    private static Integer size(String resource){
        Resource res = RESOURCE_QUEUE.get(resource);
        return res == null ? 0 : res.getQueue().size();
    }

    public static synchronized Integer size(){
        int result = 0;
        for(Resource res : RESOURCE_QUEUE.values()){
            result = res.getQueue().size() + result;
        }
        return result;
    }

    public static Collection<InstanceDO> values(String resource){
        Resource res = RESOURCE_QUEUE.get(resource);
        return res == null ? null : res.queue.values();
    }

    public static synchronized Collection<InstanceDO> values(){
        Collection<InstanceDO> list = new ArrayList<InstanceDO>();
        for(Resource runningQueue : RESOURCE_QUEUE.values()){
            list.addAll(runningQueue.queue.values());
        }
        return list;
    }

    public static Set<Map.Entry<String, InstanceDO>> entrySet(String resource){
        Resource res = RESOURCE_QUEUE.get(resource);
        return res == null ? null : res.queue.entrySet();
    }

    private static Map<String, InstanceDO> getAllInstance(){
        Map<String,InstanceDO> map = new ConcurrentHashMap<String, InstanceDO>();
        for(Resource res : RESOURCE_QUEUE.values()){
            map.putAll(res.getQueue());
        }
        return map;
    }

}
