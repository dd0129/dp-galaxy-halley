package com.dianping.data.warehouse.core.common;

import java.util.ArrayList;
import java.util.List;

import com.dianping.data.warehouse.halley.domain.TaskDO;
import com.dianping.data.warehouse.halley.domain.TaskRelaDO;

public class TaskTestUtils {
	
	public static TaskDO createExpectTaskDO(List<TaskRelaDO> relaDOList){
		TaskDO expect = new TaskDO();
		expect.setAddTime("2012-05-09 12:16:34.000000");
		expect.setAddUser("hongdi.tang");
		expect.setCycle("D");
		expect.setDatabaseSrc("mysql_DianPingBI");
		expect.setFreq("10 5 0 * * ?");
		expect.setIfPre(0);
		expect.setIfRecall(1);
		expect.setIfVal(1);
		expect.setIfWait(1);
		expect.setLogFile("bi_data_tg_bussiness_city");
		expect.setLogHome("${wormhole_log_home}/wormhole");
		expect.setOffset("D0");
		expect.setOffsetType("offset");
		expect.setOwner("yifan.cao");
		expect.setPara1("\\\"10001 mysql greenplum \\\"");
		expect.setPara2("\\\"/data/deploy/dwarch/conf/ETL/job/auto_etl/tuangou/BI_Data_TG_Bussiness_City.xml\\\"");
		expect.setPara3("\\\" \\\"  \\\"${task_id}\\\" \\\"${cal_dt}\\\"");
		expect.setPrioLvl(3);
		expect.setRecallCode("203;305;605;22;300;1;255;301;600;601;602;603;607;606;254;604;306");
		expect.setRecallInterval(10);
		expect.setRecallLimit(10);
		expect.setRemark("test");
		expect.setSuccessCode("0;201");
		expect.setTableName("bi_data_tg_bussiness_city");
		expect.setTaskGroupId(1);
		expect.setTaskId(10001);
		expect.setTaskName("mysql2gp##BI_Data_TG_Bussiness_City");
		expect.setTaskObj("ssh -o ConnectTimeout=3 -o ConnectionAttempts=5 -o PasswordAuthentication=no -o StrictHostKeyChecking=no -p 58422 deploy@10.1.6.49 sh /data/deploy/dwarch/conf/ETL/bin/start_autoetl.sh");
		expect.setTimeout(90);
		expect.setType(1);
		expect.setUpdateTime("2013-03-08 15:10:36.000000");
		expect.setUpdateUser("hongdi.tang");
		expect.setWaitCode("302");
		expect.setRelaDOList(relaDOList);
		return expect;
	}
	
	public static List<TaskRelaDO> createExpectedTaskRelaList(){
		List<TaskRelaDO> expect = new ArrayList<TaskRelaDO>();
		TaskRelaDO do1 = createTaskRelaDO(10001, "mysql2gp##BI_Main_ActiveUser_Daily", 10002, 
				"2014-02-13 18:41:59.000000", "yifan.cao", "D0");
		TaskRelaDO do2 = createTaskRelaDO(10001, "mysql2gp##CI_CheckIn", 10003, 
				"2014-02-13 18:41:59.000000", "yifan.cao", "D0");
		expect.add(do1);
		expect.add(do2);
		return expect;
	}
	
	private static TaskRelaDO createTaskRelaDO(Integer taskId, String taskName, Integer taskPreId, 
			String timeStamp, String owner, String cycleGap){
		TaskRelaDO taskRelaDo = new TaskRelaDO();
		taskRelaDo.setTaskId(taskId);
		taskRelaDo.setTaskName(taskName);
		taskRelaDo.setTaskPreId(taskPreId);
		taskRelaDo.setTimeStamp(timeStamp);
		taskRelaDo.setOwner(owner);
		taskRelaDo.setCycleGap(cycleGap);
		return taskRelaDo;
	}
	
	

}
