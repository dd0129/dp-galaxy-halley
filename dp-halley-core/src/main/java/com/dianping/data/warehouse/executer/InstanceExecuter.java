package com.dianping.data.warehouse.executer;

import com.dianping.data.warehouse.common.CoreConst;
import com.dianping.data.warehouse.dao.ExternalDAO;
import com.dianping.data.warehouse.dao.InstanceDAO;
import com.dianping.data.warehouse.domain.ExternalDO;
import com.dianping.data.warehouse.domain.InstanceDO;
import com.dianping.data.warehouse.external.ExternalExecuter;
import com.dianping.data.warehouse.external.ExternalExecuterImpl;
import com.dianping.data.warehouse.halley.client.Const;
import com.dianping.data.warehouse.resource.ResourceManager;
import com.dianping.data.warehouse.resource.RunningQueueManager;
import com.dianping.data.warehouse.utils.ProcessUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by adima on 14-3-24.
 */
@Service("instanceExecuter")
public class InstanceExecuter {
    private static Logger logger = LoggerFactory.getLogger(InstanceExecuter.class);
    @Resource(name="instanceDAO")
    private InstanceDAO instDAO;

    @Resource(name="externalDAO")
    private ExternalDAO extDAO;

    private InstanceDO getReadyInstance() throws Exception {
        List<InstanceDO> list = this.instDAO.getReadyInstanceList(Const.JOB_STATUS.JOB_READY.getValue());
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        for (InstanceDO inst : list) {
            if (inst != null && RunningQueueManager.isDuplicateTask(inst.getTaskId())) {
                logger.info(inst.getInstanceId() + "(" + inst.getTaskName() + ") exists duplicate task");
                continue;
            } else {
                if (RunningQueueManager.idDupliateInstance(inst.getInstanceId())) {
                    logger.info("Warning!!" + inst.getInstanceId() + "(" + inst.getTaskName() + " is already in Running Queue");
                    continue;
                }
                return inst;
            }
        }
        return null;
    }

    private Integer executeTask(InstanceDO inst){
        logger.info(inst.getInstanceId() + "(" + inst.getTaskName() + ") job starts");
        this.instDAO.updateInstnaceRunning(inst.getInstanceId(), Const.JOB_STATUS.JOB_RUNNING.getValue(), Const.JOB_STATUS.JOB_RUNNING.getDesc());
        if(inst.getType()== CoreConst.TASK_TYPE_LOAD){
            return ProcessUtils.executeWormholeCommand(inst);
        }else if(inst.getType()== CoreConst.TASK_TYPE_CALCULATE){
            return ProcessUtils.executeCommand(inst);
        }else{
            this.instDAO.updateInstnaceStatus(inst.getInstanceId(), Const.JOB_STATUS.JOB_INTERNAL_ERROR.getValue(),
                    Const.JOB_STATUS.JOB_INTERNAL_ERROR.getDesc());
            logger.error(inst.getInstanceId() + "(" + inst.getTaskName() + ") type "+inst.getType()+ "is illegal type");
            throw new IllegalArgumentException(inst.getInstanceId() + "(" + inst.getTaskName() + ") type "+inst.getType()+ "is illegal type");
        }
    }

    private boolean containCode(Integer code,String codes){
        if(StringUtils.isBlank(codes)){
            throw new NullPointerException("codes is null");
        }
        for (String tmp : codes.split(";")) {
            if (code == Integer.valueOf(tmp).intValue()) {
                return true;
            }
        }
        return false;
    }

    private void recoredInternalLog(InstanceDO inst, Integer rtn) {
        try{
            if(this.containCode(rtn,inst.getSuccessCode())){
                logger.info(inst.getInstanceId() + "(" + inst.getTaskName() + ") is success");
                this.instDAO.updateInstnaceStatus(inst.getInstanceId(), Const.JOB_STATUS.JOB_SUCCESS.getValue(),
                        Const.JOB_STATUS.JOB_SUCCESS.getDesc());
                return;
            }
            if (inst.getIfWait() == CoreConst.TASK_IF_WAIT) {
                if(this.containCode(rtn,inst.getWaitCode())){
                    logger.info(inst.getInstanceId() + "(" + inst.getTaskName() + ") retcode "+rtn+" is wait");
                    this.instDAO.updateInstnaceStatus(inst.getInstanceId(), Const.JOB_STATUS.JOB_SUCCESS.getValue(),
                            Const.JOB_STATUS.JOB_SUCCESS.getDesc());
                    return;
                }
            }
            logger.info(inst.getInstanceId() + "(" + inst.getTaskName() + ") retcode "+rtn+" is fail");
            this.instDAO.updateInstnaceStatus(inst.getInstanceId(), Const.JOB_STATUS.JOB_FAIL.getValue(),
                    Const.JOB_STATUS.JOB_FAIL.getDesc());

        }catch(Throwable e){
            logger.error(inst.getInstanceId() + "(" + inst.getTaskName() + ")record log error",e);
            this.instDAO.updateInstnaceStatus(inst.getInstanceId(), Const.JOB_STATUS.JOB_INTERNAL_ERROR.getValue(),
                    Const.JOB_STATUS.JOB_INTERNAL_ERROR.getDesc());
        }

    }
    private void recoredExternalLog(InstanceDO inst, Integer rtn) {
        if(rtn != Const.CODES.FAIL.getCode()){
            this.instDAO.updateInstnaceStatus(inst.getInstanceId(), Const.JOB_STATUS.JOB_SUCCESS.getValue(),
                    Const.JOB_STATUS.JOB_SUCCESS.getDesc());
        }else{
            this.instDAO.updateInstnaceStatus(inst.getInstanceId(), Const.JOB_STATUS.JOB_FAIL.getValue(),
                    Const.JOB_STATUS.JOB_FAIL.getDesc());
        }
    }

    public void execute() {
        InstanceDO inst = null;
        boolean flag = false;
        try {
            logger.info("the task executer thread starts");
            inst = this.getReadyInstance();

            if (inst == null) {
                return;
            } else {
                flag = ResourceManager.allocate(inst.getDatabaseSrc());
                if(!flag){
                    return;
                }
                RunningQueueManager.inQueue(inst);
                logger.info("Running Queue already run " + RunningQueueManager.size() + " tasks");
                logger.info(inst.getInstanceId() + "(" + inst.getTaskName() + " join to Running Queue");
                ExternalDO extDO = this.extDAO.getExternalTasksById(inst.getTaskId());

                Integer rtn = this.executeTask(inst);
                if(containCode(rtn,inst.getSuccessCode()) && inst.getIsExternalPost() == 1){
                    ExternalExecuter ext = new ExternalExecuterImpl();
                    this.recoredExternalLog(inst,ext.execute(inst,extDO));
                }else{
                    this.recoredInternalLog(inst,rtn);
                }

                //
                //this.sendEmail(ts);
            }
        } catch (Exception e) {
            logger.error("task executer thread error", e);
        } finally {
            if(inst != null){
                RunningQueueManager.outQueue(inst);
                if(flag){
                    ResourceManager.release(inst.getDatabaseSrc());
                }
            }
            logger.info("the task executer process ends");
        }
    }
}
