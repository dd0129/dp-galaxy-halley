package com.dianping.data.warehouse.halley.service;

import com.dianping.data.warehouse.halley.domain.TaskDO;

/**
 * Created by hongdi.tang on 14-2-11.
 */
public interface TaskService {
    public TaskDO getTaskByID(Integer id);

    public TaskDO getTaskBasicInfoByID(Integer id);

    public boolean deleteTaskByID(Integer id);

    public boolean insertTask(TaskDO task);

    public boolean updateTask(TaskDO task);

    public boolean updateTaskInvalidateByID(Integer id);

    public boolean updateTaskValidateByID(Integer id);

    public Integer generateTaskID(TaskDO task);

    public void instanceCallback(String instanceId,Integer extStatus,String message);


}
