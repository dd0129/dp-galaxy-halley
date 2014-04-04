package com.dianping.data.warehouse.external;

import com.dianping.data.warehouse.common.MockData;
import com.dianping.data.warehouse.domain.ExternalDO;
import com.dianping.data.warehouse.domain.InstanceDO;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Created by hongdi.tang on 14-4-4.
 */
public class ExternalExecuterImplTest {
    @Test
    public void testSuccessExecute() throws Exception {
        InstanceDO inst = MockData.genInstance();
        ExternalDO extDO = MockData.getExtInstance();
        extDO.setImplClass("com.dianping.data.warehouse.external.SuccessClientTestImpl");
        ExternalExecuter executer = new ExternalExecuterImpl();
        Integer rtn = executer.execute(inst,extDO);
        Assert.assertEquals("300",String.valueOf(rtn));
    }

    @Test
    public void testFailExecute() throws Exception {
        InstanceDO inst = MockData.genInstance();
        ExternalDO extDO = MockData.getExtInstance();
        extDO.setImplClass("com.dianping.data.warehouse.external.FailClientTestImpl");
        ExternalExecuter executer = new ExternalExecuterImpl();
        Integer rtn = executer.execute(inst,extDO);
        Assert.assertEquals("301",String.valueOf(rtn));
    }

    @Test
    public void testExceptionExecute() throws Exception {
        InstanceDO inst = MockData.genInstance();
        ExternalDO extDO = MockData.getExtInstance();
        extDO.setImplClass("com.dianping.data.warehouse.external.ExceptionClientTestImpl");
        ExternalExecuter executer = new ExternalExecuterImpl();
        Integer rtn = executer.execute(inst,extDO);
        Assert.assertEquals("501", String.valueOf(rtn));
    }
}
