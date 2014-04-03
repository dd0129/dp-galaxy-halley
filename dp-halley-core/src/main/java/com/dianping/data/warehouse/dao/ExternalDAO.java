package com.dianping.data.warehouse.dao;

import com.dianping.data.warehouse.domain.ExternalDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * Created by adima on 14-3-22.
 */
@Component("externalDAO")
public interface ExternalDAO {
    public ExternalDO getExternalTasksById(@Param("taskId") Integer taskId);

}
