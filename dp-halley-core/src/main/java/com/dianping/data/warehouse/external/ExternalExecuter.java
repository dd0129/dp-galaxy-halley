package com.dianping.data.warehouse.external;

import com.dianping.data.warehouse.domain.ExternalDO;
import com.dianping.data.warehouse.domain.InstanceDO;

/**
 * Created by hongdi.tang on 14-4-1.
 */

public interface ExternalExecuter {

    Integer execute(InstanceDO inst,ExternalDO extTask);
}
