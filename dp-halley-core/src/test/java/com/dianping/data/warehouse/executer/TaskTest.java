package com.dianping.data.warehouse.executer;

import com.dianping.data.warehouse.common.MockData;
import com.dianping.data.warehouse.domain.InstanceDO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by adima on 14-4-10.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-spring-applicationcontext.xml")
public class TaskTest {
    @Resource(name="task")
    private Task task;

    @Test
    public void testRun() throws Exception {
        InstanceDO inst = MockData.genInstance();
        inst.setInstanceId("1000120120514");
        inst.setIsExternalPost(0);
        inst.setLogPath("e:\\data\\test.log");
        task.setInstanceDO(inst);
        task.run();
    }
}
