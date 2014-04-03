package com.dianping.data.warehouse.core.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dianping.data.warehouse.core.common.TaskTestUtils;
import com.dianping.data.warehouse.halley.domain.TaskDO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-spring-applicationcontext.xml")
public class TaskDAOTest {
	
	@Resource
	private TaskDAO taskDAO;
	
	@Test
	public void testGetTaskByID(){
		Integer id = 10001;
		TaskDO task = taskDAO.getTaskByID(id);
		assertEquals(TaskTestUtils.createExpectTaskDO(null), task);
	}
	
	@Test
	public void testGet(){
		List<Integer> taskIdList = taskDAO.getTaskIDList();
		List<Integer> result = new ArrayList<Integer>();
		result.add(10001);
		result.add(10002);
		result.add(10003);
		assertEquals(result, taskIdList);
	}
	
	@Test
	public void testDeleteTask(){
		Integer id = 10001;
		//TaskDO task = taskDAO.getTaskByID(id);
		taskDAO.deleteTask(id);
		TaskDO result = taskDAO.getTaskByID(id);
		assertNull(result);
		//restore deleted task
		//taskDAO.insertTask(task);	
	}
	
	@Test
	public void testInsertTask(){
		Integer id = 10002;
		TaskDO task = taskDAO.getTaskByID(id);
		taskDAO.deleteTask(id);
		taskDAO.insertTask(task);	
		TaskDO result = taskDAO.getTaskByID(id);
		assertEquals(task, result);
	}
	
	@Test
	public void testUpdateTask(){
		Integer id = 10002;
		//TaskDO origin = taskDAO.getTaskByID(id);
		TaskDO task = taskDAO.getTaskByID(id);
		task.setIfWait(1000);
		task.setUpdateUser("yix.zhang");
		task.setIfVal(100);
		task.setFreq("11 5 0 * * ?");
		task.setIfRecall(2);
		taskDAO.updateTask(task);
		TaskDO result = taskDAO.getTaskByID(id);
		assertEquals(task, result);
		//restore updated task
		//taskDAO.deleteTask(id);
		//taskDAO.insertTask(origin);
	}
	
	@Test
	public void testUpdateTaskStatus(){
		Integer id = 10002;
		Integer ifVal = 5;
		taskDAO.updateTaskStatus(id, ifVal);
		TaskDO result = taskDAO.getTaskByID(id);
		assertEquals(result.getIfVal(), ifVal);
	}
}
