package com.dianping.data.warehouse.halley.client;


/**
 * Created by hongdi.tang on 14-4-1.
 */
public class Const {
    public static enum JOB_STATUS{
        JOB_FAIL(-1,"FAIL"),JOB_SUCCESS(1,"SUCCESS",300),JOB_INIT(0,"INIT"),JOB_RUNNING(2,"RUNNING"),JOB_SUSPEND(3,"SUSPEND"),
        JOB_INTERNAL_ERROR(4,"INTERNAL_ERROR"),JOB_WAIT(5,"WAIT"),JOB_READY(6,"READY"),JOB_TIMEOUT(7,"TIMEOUT"),
        JOB_PRE_ERROR(8,"PRE_ERROR"),JOB_POST_ERROR(9,"POST_ERROR");
        private Integer value;
        private String desc;
        private Integer extCode;

        public Integer getExtCode() {
            return extCode;
        }

        private JOB_STATUS(Integer value,String desc){
            this.value = value;
            this.desc = desc;
        }
        private JOB_STATUS(Integer value,String desc,Integer extCode){
            this.value = value;
            this.desc = desc;
            this.extCode = extCode;
        }

        public Integer getValue() {
            return value;
        }
        public String getDesc() {
            return desc;
        }
    }

    public static enum DQC_PARAM{
        taskId,scheduleTime,taskStatusId
    }

    public static enum RETURN_STR{
        code,message
    }

    public static enum EXTERNAL_CODES {
        SUCCESS(300),FAIL(301),EXCEPTION(500);
        private Integer code;
        private EXTERNAL_CODES(Integer code){
            this.code = code;
        }
        public Integer getCode(){
            return this.code;
        }
    }

    public static final String HALLEY_CASCADE_TOPIC = "halley_cascade";

}
