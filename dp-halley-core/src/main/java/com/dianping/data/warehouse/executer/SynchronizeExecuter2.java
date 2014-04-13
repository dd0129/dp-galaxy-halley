package com.dianping.data.warehouse.executer;

import com.dianping.data.warehouse.common.CoreConst;
import com.dianping.data.warehouse.dao.InstanceDAO;
import com.dianping.data.warehouse.domain.InstanceDO;
import com.dianping.data.warehouse.halley.client.Const;
import com.dianping.data.warehouse.resource.ResourceManager2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * Created by hongdi.tang on 14-3-24.
 */
public class SynchronizeExecuter2 {
    private static final Logger logger = LoggerFactory.getLogger(SynchronizeExecuter2.class);

    @Resource(name = "instanceDAO")
    private InstanceDAO instDAO;

    public void execute() {
        try {
            logger.info("the SynchronizeExecuter thread starts");
            for (InstanceDO inst : ResourceManager2.values()) {
                Long inQueueTime = inst.getInQueueTimeMillis();
                logger.info(inst.getInstanceId() + "(" + inst.getTaskName() +
                        ") in queue time " + inst.getInQueueTimeMillis());
                try{
                    if (System.currentTimeMillis() - inQueueTime > CoreConst.WAIT_INTERVAL &&
                            inst.getStatus().intValue()!=Const.JOB_STATUS.JOB_TIMEOUT.getValue()) {
                        inst.setStatus(Const.JOB_STATUS.JOB_TIMEOUT.getValue());
                        Integer status = instDAO.getInstanceInfo(inst.getInstanceId()).getStatus();
                        if (status != Const.JOB_STATUS.JOB_RUNNING.getValue() &&
                                status != Const.JOB_STATUS.JOB_TIMEOUT.getValue()) {
                            logger.info(inst.getInstanceId() + "(" + inst.getTaskName() + ") status is, " + inst.getStatus() + " in queue time at" + String.valueOf(inst.getInQueueTimeMillis()) + " has been removed");
                            ResourceManager2.outQueue(inst);
                        }
                    }
                }catch(Exception e){
                    logger.error(inst.getInstanceId() + "(" + inst.getTaskName() + ") synchronize error",e);
                }
            }
        } finally {
            logger.info("the synchronize thread ends");
        }
    }

}
