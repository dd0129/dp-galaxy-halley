package com.dianping.data.warehouse.external;

import com.dianping.data.warehouse.common.CoreConst;
import com.dianping.data.warehouse.domain.ExternalDO;
import com.dianping.data.warehouse.domain.InstanceDO;
import com.dianping.data.warehouse.halley.client.Const;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hongdi.tang on 14-4-1.
 */

public class ExternalExecuterImpl implements ExternalExecuter{
    private static Logger logger = LoggerFactory.getLogger(ExternalExecuterImpl.class);

    public Integer execute(InstanceDO inst ,ExternalDO extTask) {
        try{
            ExternalClassloader loader = new ExternalClassloader(CoreConst.EXTERNAL_CLASSPATH);
            Class clazz = loader.loadClass(extTask.getImplClass());

            Method method = clazz.getDeclaredMethod("run", Map.class);
            Map<String,String> paras = new HashMap<String, String>();

            paras.put(Const.DQC_PARAM.taskId.toString(),String.valueOf(inst.getTaskId()));
            paras.put(Const.DQC_PARAM.scheduleTime.toString(),String.valueOf(inst.getTriggerTime()));
            paras.put(Const.DQC_PARAM.taskStatusId.toString(),inst.getInstanceId());

            String rtnStr = (String)method.invoke(clazz.newInstance(),paras);
            JSONObject rtnJson = JSONObject.fromObject(rtnStr);
            String code = (String)rtnJson.get("code");
            String message = (String)rtnJson.get("message");
            this.writeLogFile(inst, message);
            return Integer.valueOf(code);
        }catch(Exception e){
            logger.error("external call error",e);
            //return Const.JOB_STATUS.JOB_SUCCESS.getValue();
            throw new RuntimeException("external call error",e);
        }
    }

    private void writeLogFile(InstanceDO inst,String msg){
        String logFile = inst.getLogPath();
        String snapshotLog = inst.getLogPath() + ".snapshot";
        try{
            FileUtils.writeStringToFile(new File(logFile),msg,"utf-8");
            FileUtils.writeStringToFile(new File(snapshotLog),msg,"utf-8");
        }catch(IOException e){
            logger.error("write external msg error",e);
        }
    }

}
