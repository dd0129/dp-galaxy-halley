package com.dianping.data.warehouse.core.dao;

import java.util.List;

import com.dianping.data.warehouse.halley.domain.TaskRelaDO;

public interface TaskRelaDAO {
	
	public List<TaskRelaDO> getTaskRelaByID(int id);
	
	public void insertTaskRela(List<TaskRelaDO> list);
	
	public void deleteTaskRela(int id);

}
