package com.dianping.data.warehouse.core.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dianping.data.warehouse.halley.domain.TaskDO;

public interface TaskDAO {
	
	public TaskDO getTaskByID(int id);
	
	public List<Integer> getTaskIDList();
	
	public void insertTask(TaskDO taskDO);
	
	public void updateTask(TaskDO taskDO);
	
	public void updateTaskStatus(@Param("taskId")int taskId, @Param("status")int status);
	
	public void deleteTask(int id);

}
