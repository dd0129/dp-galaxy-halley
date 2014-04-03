package com.dianping.data.warehouse.core.service.pigeon;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dianping.data.warehouse.core.common.TaskTestUtils;
import com.dianping.data.warehouse.halley.domain.TaskDO;
import com.dianping.data.warehouse.halley.service.TaskService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-spring-applicationcontext.xml")
public class TaskServicePigeonTest {
	
	@Resource(name="taskServicePigeon")
	private TaskService taskService;
	
	@Test
	public void testGetTaskID(){
		int id = 10001;
		TaskDO result = taskService.getTaskByID(id);
		TaskDO expected = TaskTestUtils.createExpectTaskDO(TaskTestUtils.createExpectedTaskRelaList());
		assertEquals(expected, result);
	}
	
    @Test
    public void testGenerateTaskIDNormalCase(){
    	TaskDO taskDO = TaskTestUtils.createExpectTaskDO(TaskTestUtils.createExpectedTaskRelaList());
    	Integer result = taskService.generateTaskID(taskDO);
    	Integer expected = 10004;
    	assertEquals(expected, result);
    }

}
