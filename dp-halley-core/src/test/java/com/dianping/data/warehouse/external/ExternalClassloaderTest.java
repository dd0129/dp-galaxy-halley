package com.dianping.data.warehouse.external;

import com.dianping.data.warehouse.common.CoreConst;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Created by hongdi.tang on 14-4-4.
 */
public class ExternalClassloaderTest {

    @Test
    public void testSuccessLoadClass() throws Exception{
        String className = "com.dianping.data.warehouse.external.SuccessClientTestImpl";
        ExternalClassloader loader = new ExternalClassloader(CoreConst.EXTERNAL_CLASSPATH);
        Class clazz = loader.loadClass(className);
        System.out.println(clazz.toString());
        Assert.assertEquals(clazz.getName(), className);
    }


}
