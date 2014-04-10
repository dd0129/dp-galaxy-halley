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
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by adima on 14-4-9.
 */
@Component
@Scope("prototype")
public class Task implements Runnable{
    private static Logger logger = LoggerFactory.getLogger(Task.class);

    public void clone(Task task){
        this.instDAO = task.getInstDAO();
        this.extDAO = task.getExtDAO();
    }

    public InstanceDAO getInstDAO() {
        return instDAO;
    }

    public void setInstDAO(InstanceDAO instDAO) {
        this.instDAO = instDAO;
    }

    public ExternalDAO getExtDAO() {
        return extDAO;
    }

    public void setExtDAO(ExternalDAO extDAO) {
        this.extDAO = extDAO;
    }

    @Resource(name="instanceDAO")
    private InstanceDAO instDAO;

    @Resource(name="externalDAO")
    private ExternalDAO extDAO;

    private InstanceDO inst;

    public void setInstanceDO(InstanceDO inst){
        this.inst = inst;
    }

    public void run() {
        boolean flag = false;
        try {
            logger.info("the task"+inst.getInstanceId()+" executer thread starts");
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

                Integer rtn = this.executeTask();
                if(containCode(rtn,inst.getSuccessCode()) && inst.getIsExternalPost() == 1){
                    ExternalDO extDO = this.extDAO.getExternalTasksById(inst.getTaskId());

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
            logger.info("the task"+inst.getInstanceId()+" executer thread ends");
        }
    }

    private Integer executeTask(){
        logger.info(inst.getInstanceId() + "(" + inst.getTaskName() + ") job starts");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currTime = formatter.format(new Date());
        this.instDAO.updateInstnaceRunning(inst.getInstanceId(), Const.JOB_STATUS.JOB_RUNNING.getValue(), Const.JOB_STATUS.JOB_RUNNING.getDesc(),currTime);
        if(inst.getType()== CoreConst.TASK_TYPE_LOAD){
            return ProcessUtils.executeWormholeCommand(inst);
        }else if(inst.getType()== CoreConst.TASK_TYPE_CALCULATE){
            return ProcessUtils.executeCommand(inst);
        }else{
            this.instDAO.updateInstEndStatus(inst.getInstanceId(), Const.JOB_STATUS.JOB_INTERNAL_ERROR.getValue(),
                    Const.JOB_STATUS.JOB_INTERNAL_ERROR.getDesc(),currTime);
            logger.error(inst.getInstanceId() + "(" + inst.getTaskName() + ") type "+inst.getType()+ "is illegal type");
            throw new IllegalArgumentException(inst.getInstanceId() + "(" + inst.getTaskName() + ") type "+inst.getType()+ "is illegal type");
        }
    }

    public boolean containCode(Integer code,String codes){
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

    public void recoredInternalLog(InstanceDO inst, Integer rtn) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currTime = formatter.format(new Date());
        try{
            if(this.containCode(rtn,inst.getSuccessCode())){
                logger.info(inst.getInstanceId() + "(" + inst.getTaskName() + ") is success");
                this.instDAO.updateInstEndStatus(inst.getInstanceId(), Const.JOB_STATUS.JOB_SUCCESS.getValue(),
                        Const.JOB_STATUS.JOB_SUCCESS.getDesc(),currTime);
                return;
            }
            if (inst.getIfWait() == CoreConst.TASK_IF_WAIT) {
                if(this.containCode(rtn,inst.getWaitCode())){
                    logger.info(inst.getInstanceId() + "(" + inst.getTaskName() + ") retcode "+rtn+" is wait");
                    this.instDAO.updateInstEndStatus(inst.getInstanceId(), Const.JOB_STATUS.JOB_WAIT.getValue(),
                            Const.JOB_STATUS.JOB_WAIT.getDesc(),currTime);
                    return;
                }
            }
            logger.info(inst.getInstanceId() + "(" + inst.getTaskName() + ") retcode "+rtn+" is fail");
            this.instDAO.updateInstEndStatus(inst.getInstanceId(), Const.JOB_STATUS.JOB_FAIL.getValue(),
                    Const.JOB_STATUS.JOB_FAIL.getDesc(),currTime);

        }catch(Throwable e){
            logger.error(inst.getInstanceId() + "(" + inst.getTaskName() + ") record log error",e);
            this.instDAO.updateInstEndStatus(inst.getInstanceId(), Const.JOB_STATUS.JOB_INTERNAL_ERROR.getValue(),
                    Const.JOB_STATUS.JOB_INTERNAL_ERROR.getDesc(),currTime);
        }
    }
    public void recoredExternalLog(InstanceDO inst, Integer rtn) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currTime = formatter.format(new Date());
        if(rtn == null || rtn != Const.EXTERNAL_CODES.FAIL.getCode().intValue()){
            this.instDAO.updateInstEndStatus(inst.getInstanceId(), Const.JOB_STATUS.JOB_SUCCESS.getValue(),
                    Const.JOB_STATUS.JOB_SUCCESS.getDesc(),currTime);
        }else{
            this.instDAO.updateInstEndStatus(inst.getInstanceId(), Const.JOB_STATUS.JOB_FAIL.getValue(),
                    Const.JOB_STATUS.JOB_FAIL.getDesc(),currTime);
        }
    }
}
