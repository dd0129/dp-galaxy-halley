package com.dianping.data.warehouse.executer;

import com.dianping.data.warehouse.common.CoreConst;
import com.dianping.data.warehouse.dao.InstanceDAO;
import com.dianping.data.warehouse.domain.InstanceDO;
import com.dianping.data.warehouse.halley.client.Const;
import com.dianping.data.warehouse.resource.ResourceManager;
import com.dianping.data.warehouse.resource.RunningQueueManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by hongdi.tang on 14-3-24.
 */
@Service("synchronizeExecuter")
public class SynchronizeExecuter {
    private static final Logger logger = LoggerFactory.getLogger(SynchronizeExecuter.class);

    @Resource(name = "instanceDAO")
    private InstanceDAO instDAO;

    public void execute() {
        boolean flag = false;
        try {
            logger.info("the SynchronizeExecuter thread starts");
            for (Map.Entry<String, InstanceDO> entry : RunningQueueManager.entrySet()) {
                InstanceDO inst = entry.getValue();
                Long inQueueTime = inst.getInQueueTimeMillis();
                logger.info(inst.getInstanceId() + "(" + inst.getTaskName() +
                        ") in queue time " + inst.getInQueueTimeMillis());
                try{
                    if (System.currentTimeMillis() - inQueueTime > CoreConst.WAIT_INTERVAL) {
                        Integer status = instDAO.getInstanceInfo(inst.getInstanceId()).getStatus();
                        if (status != Const.JOB_STATUS.JOB_RUNNING.getValue() &&
                                status != Const.JOB_STATUS.JOB_TIMEOUT.getValue()) {
                            logger.info(inst.getInstanceId() + "(" + inst.getTaskName() + ") status is, "+inst.getStatus() +" in queue time at"+ String.valueOf(inst.getInQueueTimeMillis()) + " has been kicked");
                            RunningQueueManager.outQueue(inst);
                            flag = true;
                        }
                    }
                }catch(Exception e){
                    logger.error(inst.getInstanceId() + "(" + inst.getTaskName() + ") synchronize error",e);
                }finally{
                    if(inst!= null && flag){
                        ResourceManager.release(inst.getDatabaseSrc());
                        logger.info(inst.getInstanceId() + "(" + inst.getTaskName() + ") release resource");
                    }
                }
            }
        } finally {
            logger.info("the SynchronizeExecuter thread ends");
        }
    }

}
