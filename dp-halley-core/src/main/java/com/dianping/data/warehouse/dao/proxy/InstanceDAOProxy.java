package com.dianping.data.warehouse.dao.proxy;

import com.dianping.data.warehouse.dao.InstanceDAO;
import com.dianping.data.warehouse.domain.InstanceDO;
import com.dianping.data.warehouse.domain.TaskDO;
import com.dianping.data.warehouse.domain.TaskRelaDO;
import com.dianping.data.warehouse.executer.InitExecuter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by hongdi.tang on 14-3-28.
 */
@Component("instanceDAOProxy")
public class InstanceDAOProxy {
    @Resource(name="instanceDAO")
    private InstanceDAO instDAO;

    public void saveInstance(InstanceDO inst){
        instDAO.saveInstance(inst);
        if(!CollectionUtils.isEmpty(inst.getInstRelaList())){
            instDAO.saveInstanceRela(inst.getInstRelaList());
        }
    }

    public void saveList(List<TaskDO> list,Map<Integer,List<TaskRelaDO>> relaMap,
                         InitExecuter instanceInit) throws Exception{
        Method method = InitExecuter.class.getDeclaredMethod("generateInstance", TaskDO.class, List.class, Date.class);
        method.setAccessible(true);
        for(TaskDO task : list){
            List<TaskRelaDO> relaList = relaMap.get(task.getTaskId());
            InstanceDO output = (InstanceDO)method.invoke(instanceInit,task,relaList,new Date());
            output.setRunningPrio(0);
            this.saveInstance(output);
        }
    }
}
