package com.dianping.data.warehouse.executer;

import com.dianping.data.warehouse.common.CoreConst;
import com.dianping.data.warehouse.dao.ExternalDAO;
import com.dianping.data.warehouse.dao.InstanceDAO;
import com.dianping.data.warehouse.domain.ExternalDO;
import com.dianping.data.warehouse.domain.InstanceDO;
import com.dianping.data.warehouse.external.ExternalExecuter;
import com.dianping.data.warehouse.external.ExternalExecuterImpl;
import com.dianping.data.warehouse.halley.client.Const;
import com.dianping.data.warehouse.resource.ResourceManager;
import com.dianping.data.warehouse.resource.ResourceManager2;
import com.dianping.data.warehouse.resource.RunningQueueManager;
import com.dianping.data.warehouse.resource.ThreadPoolManager;
import com.dianping.data.warehouse.utils.ProcessUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by adima on 14-3-24.
 */
@Service("instanceExecuter2")
public class InstanceExecuter2 {
    private static Logger logger = LoggerFactory.getLogger(InstanceExecuter2.class);
    @Resource(name="instanceDAO")
    private InstanceDAO instDAO;

    private ThreadPoolTaskExecutor taskExecutor;
    @Resource(name="task")
    private Task task1;

    @Autowired
    public InstanceExecuter2(@Value("#{taskExecutor}") ThreadPoolTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void execute() throws Exception{
        List<InstanceDO> list = this.instDAO.getReadyInstanceList(Const.JOB_STATUS.JOB_READY.getValue());
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        for (InstanceDO inst : list) {
            if (inst != null && RunningQueueManager.isDuplicateTask(inst.getTaskId())) {
                logger.info(inst.getInstanceId() + "(" + inst.getTaskName() + ") exists duplicate task");
                continue;
            } else {
                if (RunningQueueManager.idDupliateInstance(inst.getInstanceId())) {
                    logger.info("Warning!!" + inst.getInstanceId() + "(" + inst.getTaskName() + " is already in Running Queue");
                    continue;
                }
                inst.setIsExternalPost(0);
                inst.setLogPath("e:\\data\\" + inst.getTaskName() + "." + inst.getInstanceId());
                inst.setTaskObj("e:/data/test.bat");
                Task task = new Task();
                task.clone(task1);
                task.setInstanceDO(inst);
                logger.info(task.toString());
//                ThreadPoolManager.registerTask(task);
                //Thread.sleep(100);
                taskExecutor.submit(task);
            }
        }

    }
}
