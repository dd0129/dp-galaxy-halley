package com.dianping.data.warehouse.core.service;

import com.dianping.data.warehouse.core.common.HalleyConst;
import com.dianping.data.warehouse.core.common.ValidatorUtils;
import com.dianping.data.warehouse.core.dao.InstanceDAO;
import com.dianping.data.warehouse.core.dao.TaskDAO;
import com.dianping.data.warehouse.core.dao.TaskRelaDAO;
import com.dianping.data.warehouse.halley.client.Const;
import com.dianping.data.warehouse.halley.domain.TaskDO;
import com.dianping.data.warehouse.halley.domain.TaskRelaDO;
import com.dianping.data.warehouse.halley.service.TaskService;
import com.dianping.pigeon.remoting.provider.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by hongdi.tang on 14-2-12.
 */

@Service
@Transactional
public class TaskServiceImpl implements TaskService{
    private static Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Resource
    private TaskDAO taskDAO;
    
    @Resource
    private TaskRelaDAO taskRelaDAO;

    @Resource
    private InstanceDAO instDAO;

    public TaskDAO getTaskDAO() {
        return taskDAO;
    }

    public void setTaskDAO(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    public TaskRelaDAO getTaskRelaDAO() {
        return taskRelaDAO;
    }

    public InstanceDAO getInstDAO() {
        return instDAO;
    }

    public void setInstDAO(InstanceDAO instDAO) {
        this.instDAO = instDAO;
    }

    public void setTaskRelaDAO(TaskRelaDAO taskRelaDAO) {
		this.taskRelaDAO = taskRelaDAO;
	}

	@Override
    public TaskDO getTaskByID(Integer id) {
        logger.info("query task success");
        TaskDO taskDO = taskDAO.getTaskByID(id);
        if(taskDO == null)  return null;
        List<TaskRelaDO> list = taskRelaDAO.getTaskRelaByID(id);
        taskDO.setRelaDOList(list);
        return taskDO;
    }

    @Override
    public TaskDO getTaskBasicInfoByID(Integer id) {
        return taskDAO.getTaskByID(id);
    }

    @Override
    public boolean deleteTaskByID(Integer id) {
        taskDAO.deleteTask(id);
        taskRelaDAO.deleteTaskRela(id);
        return true;
    }

    @Override
    public boolean insertTask(TaskDO task) {
        ValidatorUtils.validateModel(task);
        taskDAO.insertTask(task);
        if(task.getRelaDOList() != null && task.getRelaDOList().size()>0){
            taskRelaDAO.insertTaskRela(task.getRelaDOList());
        }
        logger.info("save task success");
        return true;
    }

    @Override
    public boolean updateTask(TaskDO task) {
        taskDAO.updateTask(task);
        if(task.getRelaDOList()!= null && task.getRelaDOList().size()>0){
            taskRelaDAO.deleteTaskRela(task.getTaskId());
            taskRelaDAO.insertTaskRela(task.getRelaDOList());
        }
        return true;
    }

    @Override
    public boolean updateTaskInvalidateByID(Integer id) {
            taskDAO.updateTaskStatus(id, 2);
            return true;
    }

    @Override
    public boolean updateTaskValidateByID(Integer id) {
            taskDAO.updateTaskStatus(id, 1);
            return true;
    }

    @Override
    public Integer generateTaskID(TaskDO taskEntity) {
        int taskGroupId = taskEntity.getTaskGroupId();
        String databaseSrc = taskEntity.getDatabaseSrc();
        int iDatabaseSrc = 0;
        if (databaseSrc.equalsIgnoreCase(HalleyConst.DATABASE_TYPE_HIVE)) {
            iDatabaseSrc = 1;
        } else if (databaseSrc.equalsIgnoreCase(HalleyConst.DATABASE_TYPE_GP57)) {
            iDatabaseSrc = 2;
        } else if (databaseSrc.equalsIgnoreCase(HalleyConst.DATABASE_TYPE_GP59)) {
            iDatabaseSrc = 3;
        }
        List<Integer> taskIds = new LinkedList<Integer>();

        for (Integer taskId : taskDAO.getTaskIDList()){
            if (taskGroupId == 1 && taskId < 100000) {
                taskIds.add(taskId);
            } else if (taskId / 100000 == taskGroupId && taskId % 10 == iDatabaseSrc) {
                taskIds.add(taskId / 10);
            }
        }
        int taskID = -1;
        for (int i = taskGroupId * 10000 + 1; i < (taskGroupId + 1) * 10000; ++i) {
            if (!taskIds.contains(i)) {
                taskID = i;
                break;
            }
        }
        if (taskGroupId != 1) {
            taskID = taskID * 10 + iDatabaseSrc;
        }
        return taskID;
    }

    @Override
    public void instanceCallback(String instanceId, Integer rtnCode,String message) {
        if(rtnCode == Const.EXTERNAL_CODES.SUCCESS.getCode()){
            instDAO.updateInstnaceStatus(instanceId,Const.JOB_STATUS.JOB_SUCCESS.getValue(),
                    Const.JOB_STATUS.JOB_SUCCESS.getDesc());
        }else{
            instDAO.updateInstnaceStatus(instanceId,Const.JOB_STATUS.JOB_POST_ERROR.getValue(),
                    Const.JOB_STATUS.JOB_POST_ERROR.getDesc());
        }
    }


}
