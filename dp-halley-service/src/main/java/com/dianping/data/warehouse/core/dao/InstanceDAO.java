package com.dianping.data.warehouse.core.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * Created by adima on 14-3-23.
 */
@Component("instanceDAO")
public interface InstanceDAO {
    public void updateInstnaceStatus(@Param("instanceId") String instanceId, @Param("status") Integer status, @Param("desc") String desc);

}
