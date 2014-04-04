package com.dianping.data.warehouse.core.service;

import com.dianping.data.warehouse.core.common.TaskTestUtils;
import com.dianping.data.warehouse.core.dao.TaskRelaDAO;
import com.dianping.data.warehouse.halley.domain.TaskDO;
import com.dianping.data.warehouse.halley.domain.TaskRelaDO;
import com.dianping.data.warehouse.halley.service.TaskService;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-spring-applicationcontext.xml")
public class TaskServiceImplTest{
	
	@Resource(name="taskService")
	private TaskService taskService;
	
	@Test
	public void testGetTaskID(){
		int id = 10001;
		TaskDO result = taskService.getTaskByID(id);
		TaskDO expected = TaskTestUtils.createExpectTaskDO(TaskTestUtils.createExpectedTaskRelaList());
		assertEquals(expected, result);
	}

    @Test
    public void testUpdateTask(){
        List<TaskRelaDO> relaDOList = TaskTestUtils.createExpectedTaskRelaList();
        TaskDO task = TaskTestUtils.createExpectTaskDO(relaDOList);
        task.setUpdateUser("shanshan");
        try{
            taskService.updateTask(task);
        } catch(RuntimeException e){
            e.printStackTrace();
        }
        TaskDO task1 = taskService.getTaskByID(10001);
        Assert.assertEquals(task1.getAddUser(),"hongdi.tang");
    }


	private TaskRelaDAO replaceTaskRelaDAOByMock(){
		TaskRelaDAO mockTaskRelaDAO = Mockito.mock(TaskRelaDAO.class);
		List<TaskRelaDO> relaDOList = TaskTestUtils.createExpectedTaskRelaList();
    	doThrow(new RuntimeException()).when(mockTaskRelaDAO).deleteTaskRela(10001);
    	doThrow(new RuntimeException()).when(mockTaskRelaDAO).insertTaskRela(relaDOList);
    	when(mockTaskRelaDAO.getTaskRelaByID(10001)).thenReturn(relaDOList);
    	if(!(taskService instanceof TaskServiceImpl)) fail();
    	TaskServiceImpl impl = ((TaskServiceImpl)taskService);
    	TaskRelaDAO origin = impl.getTaskRelaDAO();
    	impl.setTaskRelaDAO(mockTaskRelaDAO);
    	return origin;
	}
	
	private void restoreTaskRelaDAO(TaskRelaDAO origin){
		if(!(taskService instanceof TaskServiceImpl)) fail();
    	TaskServiceImpl impl = ((TaskServiceImpl)taskService);
    	impl.setTaskRelaDAO(origin);
	}

    @Test
	public void testDeleteTaskByIDTransactional(){
    	TaskRelaDAO origin = replaceTaskRelaDAOByMock();
    	try{
    		taskService.deleteTaskByID(10001);
    		fail("taskService.deleteTaskByID does not throw exception as expected");
    	} catch(RuntimeException ex){}
    	restoreTaskRelaDAO(origin);
    	
    	TaskDO taskDO = taskService.getTaskByID(10001);
    	assertNotNull(taskDO);
    }
    
    @Test
    public void testInsertTaskTransactional(){
    	taskService.deleteTaskByID(10001);
    	TaskDO result = taskService.getTaskByID(10001);
    	assertNull(result);
    	TaskRelaDAO origin = replaceTaskRelaDAOByMock();
    	try{
    		taskService.insertTask(TaskTestUtils.createExpectTaskDO(TaskTestUtils.createExpectedTaskRelaList()));
    		fail("taskService.insertTask does not throw exception as expected");
    	}catch(RuntimeException ex){
    		System.out.println("exception catched");
    	}
    	restoreTaskRelaDAO(origin);
    	result = taskService.getTaskByID(10001);
    	assertNull(result);
    	
    	taskService.insertTask(TaskTestUtils.createExpectTaskDO(null));
    }
    
    @Test
    public void testUpdateTaskTransactional(){
    	TaskDO expected = taskService.getTaskByID(10001);
    	TaskRelaDAO origin = replaceTaskRelaDAOByMock();
    	try{
    		TaskDO insertTaskDO = TaskTestUtils.createExpectTaskDO(TaskTestUtils.createExpectedTaskRelaList());
    		insertTaskDO.setTaskName("XXXXXXXXX");
    		insertTaskDO.setTableName("XXXXXXXXX");
    		taskService.insertTask(insertTaskDO);
    		fail("taskService.insertTask does not throw exception as expected");
    	}catch(RuntimeException ex){
    		System.out.println("exception catched");
    	}
    	restoreTaskRelaDAO(origin);
    	TaskDO result = taskService.getTaskByID(10001);
    	assertEquals(expected, result);
    }
    
    @Test
    public void testGenerateTaskIDNormalCase(){
    	TaskDO taskDO = TaskTestUtils.createExpectTaskDO(TaskTestUtils.createExpectedTaskRelaList());
    	Integer result = taskService.generateTaskID(taskDO);
    	Integer expected = 10004;
    	assertEquals(expected, result);
    }

    @Test
    public void testInstanceCallback(){
    	taskService.instanceCallback("1000120120601",300,"success");
        Assert.assertTrue(true);
    }


}
