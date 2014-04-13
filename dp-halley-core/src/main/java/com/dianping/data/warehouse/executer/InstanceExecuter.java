package com.dianping.data.warehouse.executer;

import com.dianping.data.warehouse.dao.ExternalDAO;
import com.dianping.data.warehouse.dao.InstanceDAO;
import com.dianping.data.warehouse.domain.InstanceDO;
import com.dianping.data.warehouse.halley.client.Const;
import com.dianping.data.warehouse.resource.ResourceManager2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by adima on 14-3-24.
 */
public class InstanceExecuter {
    private static Logger logger = LoggerFactory.getLogger(InstanceExecuter.class);
    @Resource(name="instanceDAO")
    private InstanceDAO instDAO;

    @Resource(name="externalDAO")
    private ExternalDAO extDAO;

    private ThreadPoolTaskExecutor taskExecutor;

    public InstanceExecuter(ThreadPoolTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void execute() throws Exception{
        try{
            logger.info("the execute thread starts");
            List<InstanceDO> list = this.instDAO.getReadyInstanceList(Const.JOB_STATUS.JOB_READY.getValue());
            if (CollectionUtils.isEmpty(list)) {
                return;
            }

            for (InstanceDO inst : list) {
                if (inst == null) {
                    continue;
                }else if(ResourceManager2.isDuplicateTask(inst)) {
                    logger.info(inst.getInstanceId() + "(" + inst.getTaskName() + ") exists duplicate task");
                    continue;
                } else {
                    if (ResourceManager2.idDupliateInstance(inst)) {
                        logger.info("Warning!!" + inst.getInstanceId() + "(" + inst.getTaskName() + " is already in Running Queue");
                        continue;
                    }
                }

                Task2 task = new Task2(instDAO,extDAO,inst);
                Thread.sleep(100);
                taskExecutor.submit(task);
            }
        }finally{
            logger.info("the execute thread ends");
        }


    }
}
