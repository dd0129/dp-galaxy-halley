package com.dianping.data.warehouse.core.service;

import com.dianping.data.warehouse.halley.service.HalleyProducer;
import org.junit.Test;

/**
 * Created by hongdi.tang on 14-4-3.
 */

public class TaskProducerImplTest {
    @Test
    public void testPublish() throws Exception {
        HalleyProducer producer = new TaskProducerImpl();
        producer.publish("fuck");
    }

}
