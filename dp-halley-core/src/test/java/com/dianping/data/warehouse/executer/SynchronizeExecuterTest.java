package com.dianping.data.warehouse.executer;

import com.dianping.data.warehouse.common.MockData;
import com.dianping.data.warehouse.domain.InstanceDO;
import com.dianping.data.warehouse.resource.ResourceManager;
import com.dianping.data.warehouse.resource.RunningQueueManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by hongdi.tang on 14-3-31.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-spring-applicationcontext.xml")
public class SynchronizeExecuterTest {

    @Resource(name = "synchronizeExecuter")
    private SynchronizeExecuter service;

    @Before
    public void setUp() throws Exception {
        InstanceDO inst = MockData.genInstance();
        inst.setInstanceId("1000120120522");
        inst.setInQueueTimeMillis(System.currentTimeMillis());
        RunningQueueManager.inQueue(inst);
        ResourceManager.allocate(inst.getDatabaseSrc());

        InstanceDO inst1 = MockData.genInstance();
        inst1.setInstanceId("1000120120601");
        inst1.setInQueueTimeMillis(System.currentTimeMillis()- 3600*1000*5);
        RunningQueueManager.inQueue(inst1);
        ResourceManager.allocate(inst.getDatabaseSrc());
    }

    @Test
    public void testExecute() throws Exception {
        service.execute();
    }
}
