package com.dianping.data.warehouse.executer;

import com.dianping.data.warehouse.common.MockData;
import com.dianping.data.warehouse.dao.ExternalDAO;
import com.dianping.data.warehouse.dao.InstanceDAO;
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
    @Resource(name="instanceDAO")
    private InstanceDAO instDAO;

    @Resource(name="externalDAO")
    private ExternalDAO extDAO;

    @Test
    public void testRun() throws Exception {
        InstanceDO inst = MockData.genInstance();
        inst.setInstanceId("1000120120514");
        inst.setIsExternalPost(0);
        inst.setLogPath("e:\\data\\test.log");
        Task2 task = new Task2(instDAO,extDAO,inst);
        task.run();
    }
}
