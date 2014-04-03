package com.dianping.data.warehouse.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hongdi.tang on 14-3-15.
 */
public class ResourceManager {
    private ResourceManager(){}
    private static Logger logger = LoggerFactory.getLogger(ResourceManager.class);
    private static final String RESOURCE_FILE = "conf/resource.properties";

    private static Map<String,Resource> RESOURCE_MAP = new ConcurrentHashMap<String, Resource>();

    static{
        Properties props = new Properties();
        try{
            props.load(ResourceManager.class.getClassLoader().getResourceAsStream(RESOURCE_FILE));
            for (String key : props.stringPropertyNames()){
                RESOURCE_MAP.put(key,new Resource(key, Integer.valueOf(props.getProperty(key)),0));
            }
        }catch(Exception e){
            logger.error("load resource error",e);
            throw new Error("load resource error");
        }
    }

    public static boolean allocate(String resourceName){
        Resource res = RESOURCE_MAP.get(resourceName);
        synchronized (res){
            if(res == null){
                RESOURCE_MAP.put(resourceName, new Resource(resourceName, 1, 1));
                logger.info(resourceName.concat(" join Resouce Map"));
                return true;
            } else{
                if(res.getRunningNum()<res.getCapability()){
                    res.addRunningNum();
                    logger.info(resourceName.concat(" resouce equal " ).concat(String.valueOf(res.getRunningNum())));
                    return true;
                }
                logger.info(resourceName.concat(" resouce is full; limit :=" ).concat(String.valueOf(res.getCapability())));
                return false;
            }
        }
    }

    public static void release(String resourceName){
        Resource res = RESOURCE_MAP.get(resourceName);
        synchronized (res){
            if(res == null){
                logger.info(resourceName.concat(" is not exists"));
            } else{
                res.minusRunningNum();
                logger.info(resourceName.concat(" resouce equal " ).concat(String.valueOf(res.getRunningNum())));
            }
        }
    }

    public static Resource getResource(String resource){
        return RESOURCE_MAP.get(resource);
    }

    private static class Resource{
        private String resourceName;
        private Integer capability;
        private Integer runningNum;

        public Resource(String resourceName, Integer capability, Integer runningNum) {
            this.resourceName = resourceName;
            this.capability = capability;
            this.runningNum = runningNum;
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



        public Integer getRunningNum() {
            return runningNum;
        }

        public void setRunningNum(Integer runningNum) {
            this.runningNum = runningNum;
        }

        public  void addRunningNum() {
            ++this.runningNum ;
        }

        public  void minusRunningNum() {
            --this.runningNum ;
        }
    }
}
