package com.dianping.data.warehouse.dao;

import com.dianping.data.warehouse.domain.InstanceDO;
import com.dianping.data.warehouse.domain.InstanceRelaDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by adima on 14-3-23.
 */
@Component("instanceDAO")
public interface InstanceDAO {
    public void saveInstance(InstanceDO inst);

    public void saveInstanceRela(List<InstanceRelaDO> list);

    public InstanceDO getInstanceInfo(@Param("instanceId") String instanceId);

    public List<InstanceDO> getInitInstanceList(@Param("status") Integer status,@Param("triggerTime") Long triggerTime);

    public List<InstanceDO> getReadyInstanceList(@Param("status") Integer status);

    public void updateInstnaceStatus(@Param("instanceId")String instanceId,@Param("status") Integer status,@Param("desc") String desc);

    public void updateInstnaceRunning(@Param("instanceId")String instanceId,@Param("status") Integer status,@Param("desc") String desc);

    public List<InstanceDO> getRelaInstanceList(@Param("instanceId") String instanceId);

    public Integer updateInstnaceListStatus(@Param("status") Integer initStatus,@Param("desc") String desc,@Param("originStatus") Integer waitStatus);

    public Integer resetInstance(@Param("status") Integer initStatus,@Param("desc") String desc,
                                            @Param("status1") Integer status1,@Param("status2") Integer status2);

}
