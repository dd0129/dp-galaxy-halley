package com.dianping.data.warehouse.executer;

import com.dianping.data.warehouse.common.MockData;
import com.dianping.data.warehouse.domain.ExternalDO;
import com.dianping.data.warehouse.domain.InstanceDO;
import com.dianping.data.warehouse.external.ExternalExecuter;
import com.dianping.data.warehouse.external.ExternalExecuterImpl;
import com.dianping.data.warehouse.resource.ResourceManager;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * Created by hongdi.tang on 14-3-31.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-spring-applicationcontext.xml")
public class InstanceExecuterTest {

    @Resource(name="instanceExecuter")
    private InstanceExecuter service;

    private static InstanceDO inst1;

    private static ExternalDO extDO;

    @BeforeClass
    public static void setUp() throws Exception {
        InstanceDO inst = MockData.genInstance();
        inst.setInstanceId("1000120120522");
        inst.setInQueueTimeMillis(System.currentTimeMillis()- 3600*1000*5);
        //RunningQueueManager.inQueue(inst);

        inst1 = MockData.genInstance();
        inst1.setInstanceId("1000120120601");
        inst1.setInQueueTimeMillis(System.currentTimeMillis());
        inst1.setType(2);
        inst1.setSuccessCode("0;10");
        inst1.setWaitCode("3;345");
        inst1.setIfWait(1);
        inst1.setIfRecall(1);
        inst1.setIsExternalPost(1);
        inst1.setIsExternalPre(1);

        extDO = MockData.getExtInstance();
        extDO.setImplClass("com.dianping.data.warehouse.external.SuccessClientTestImpl");
       // RunningQueueManager.inQueue(inst1);
    }

    @Test
    public void testExecute() throws Exception {
        service.execute();
        Assert.assertEquals(String.valueOf(ResourceManager.getResource(inst1.getDatabaseSrc())),"0");
    }

    @Test
    public void testExecuteTask() throws Exception {
        Method method = InstanceExecuter.class.getDeclaredMethod("executeTask",InstanceDO.class);
        method.setAccessible(true);
        Integer inst = (Integer)method.invoke(service,inst1);
        Assert.assertNotNull(inst);
    }

    @Test
    public void testGetReadyInstance() throws Exception {
        Method method = InstanceExecuter.class.getDeclaredMethod("getReadyInstance");
        method.setAccessible(true);
        InstanceDO inst = (InstanceDO)method.invoke(service);
        Assert.assertNotNull(inst);
    }

    @Test
    public void testInternalRecordLog() throws Exception {
        Method method = InstanceExecuter.class.getDeclaredMethod("recoredInternalLog",InstanceDO.class,Integer.class);
        method.setAccessible(true);
        method.invoke(service,inst1,null);
        method.invoke(service, inst1, 0);
        method.invoke(service, inst1, 10);
        method.invoke(service, inst1, 3);
        method.invoke(service, inst1, 33);
        method.invoke(service, inst1, 22);
        method.invoke(service, inst1, 55);
        method.invoke(service,inst1,null);
    }

    @Test
    public void testExternalRecordLog() throws Exception {
        Method method = InstanceExecuter.class.getDeclaredMethod("recoredExternalLog",InstanceDO.class,Integer.class);
        method.setAccessible(true);
        method.invoke(service, inst1, null);
        method.invoke(service, inst1, 300);
        method.invoke(service, inst1, 301);
        method.invoke(service, inst1, 501);
        method.invoke(service, inst1, 600);
        Assert.assertTrue(true);
    }

    @Test
    public void testExecuteLogic() throws Exception {
        //success
        Integer rtn = 0;
        if(service.containCode(rtn,inst1.getSuccessCode()) && inst1.getIsExternalPost() == 1){
            ExternalExecuter ext = new ExternalExecuterImpl();
            service.recoredExternalLog(inst1, ext.execute(inst1, extDO));
        }else{
            service.recoredInternalLog(inst1, rtn);
        }
        Assert.assertTrue(true);

        //direct fail
        rtn = 25;
        if(service.containCode(rtn,inst1.getSuccessCode()) && inst1.getIsExternalPost() == 1){
            ExternalExecuter ext = new ExternalExecuterImpl();
            service.recoredExternalLog(inst1, ext.execute(inst1, extDO));
        }else{
            service.recoredInternalLog(inst1, rtn);
        }
        Assert.assertTrue(true);

        rtn = 0;
        extDO.setImplClass("com.dianping.data.warehouse.external.FailClientTestImpl");
        if(service.containCode(rtn,inst1.getSuccessCode()) && inst1.getIsExternalPost() == 1){
            ExternalExecuter ext = new ExternalExecuterImpl();
            service.recoredExternalLog(inst1, ext.execute(inst1, extDO));
        }else{
            service.recoredInternalLog(inst1, rtn);
        }
        Assert.assertTrue(true);

        rtn = 0;
        extDO.setImplClass("com.dianping.data.warehouse.external.ExceptionClientTestImpl");
        if(service.containCode(rtn,inst1.getSuccessCode()) && inst1.getIsExternalPost() == 1){
            ExternalExecuter ext = new ExternalExecuterImpl();
            service.recoredExternalLog(inst1, ext.execute(inst1, extDO));
        }else{
            service.recoredInternalLog(inst1, rtn);
        }
        Assert.assertTrue(true);
    }
}
