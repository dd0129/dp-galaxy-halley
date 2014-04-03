package com.dianping.data.warehouse.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by adima on 14-3-23.
 */
public class TaskUtils {
    private static Logger logger = LoggerFactory.getLogger(TaskUtils.class);
    private TaskUtils(){}

    public static String generateInstanceID(Integer task_id, String type, Date init_date)  {
        if (type.equals("H")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
            return task_id + sdf.format(init_date);
        } else if (type.equals("D")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd00");
            return task_id + sdf.format(init_date);
        } else if (type.equals("W")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMWW00");
            return task_id + sdf.format(init_date);
        } else if (type.equals("M")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM0100");
            return task_id + sdf.format(init_date);
        } else if (type.equals("mi")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
            return task_id + sdf.format(init_date);
        } else {
            throw new IllegalArgumentException(type + " is illegal cycle type ");
        }
    }



    public static String generateRelaInstanceID(Integer pre_id, Long fire_time, String gap){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(fire_time);
        Date date = calendar.getTime();

        String type = gap.substring(0, 1);
        int interval = new Integer(gap.substring(1));

        if (type.equals("H")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.HOUR, -1 * interval);

            String pre_str_date = sdf.format(cal.getTime());

            return pre_id + pre_str_date;
        } else if (type.equals("D")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd00");
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, -1 * interval);

            String pre_str_date = sdf.format(cal.getTime());
            return pre_id + pre_str_date;
        } else if (type.equals("M")) {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMM0100");
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.MONTH, -1 * interval);

            String pre_str_date = sdf1.format(cal.getTime());
            return pre_id + pre_str_date;
        } else if (type.equals("W")) {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMWW00");
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.WEEK_OF_YEAR, -1 * interval);
            String pre_str_date = sdf1.format(cal.getTime());
            return pre_id + pre_str_date;
        } else {
            logger.error("error input cycle gap " + gap);
            return null;
        }
    }
}
