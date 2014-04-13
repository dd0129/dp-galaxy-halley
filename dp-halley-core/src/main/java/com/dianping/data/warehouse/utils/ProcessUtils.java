package com.dianping.data.warehouse.utils;

import com.dianping.data.warehouse.common.CoreConst;
import com.dianping.data.warehouse.domain.InstanceDO;
import com.dianping.data.warehouse.utils.Utilities.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by adima on 14-3-24.
 */
public class ProcessUtils {
    private static Logger logger = LoggerFactory.getLogger(ProcessUtils.class);

    public static Integer executeWormholeCommand(InstanceDO inst){
        FileOutputStream outStream = null;
        FileOutputStream snapStream = null;
        BufferedReader br = null;
        String cmdLine = StringUtils.join(new String[]{inst.getTaskObj(),inst.getPara1(),inst.getPara2(),inst.getPara3()}," ");
        logger.debug("cmdline := "+cmdLine);
        try{
            Process process = Runtime.getRuntime().exec(cmdLine);
            File outputFile = new File(inst.getLogPath());
            File snapshotOutputFile = new File(StringUtils.join(new String[]{inst.getLogPath(), "snapshot"}, "."));
            outStream = new FileOutputStream(outputFile, true);
            snapStream = new FileOutputStream(snapshotOutputFile, true);

            StreamWriter errPrinter = new StreamWriter(process.getErrorStream(), outputFile,snapshotOutputFile);
            errPrinter.start();

            br = null;
            InputStreamReader isr = new InputStreamReader(process.getInputStream(),"UTF-8");
            br = new BufferedReader(isr);
            String line = null;

            Pattern pattern = Pattern.compile("return code-");
            String rtnCodeStr = null;
            while ((line = br.readLine()) != null) {
                outStream.write((line + "\r\n").getBytes());
                snapStream.write((line + "\r\n").getBytes());
                Matcher match = pattern.matcher(line);
                logger.debug(line);
                if(match.find()){
                    rtnCodeStr = line;
                    logger.debug("rtn code str:" + line );
                }
            }
            outStream.close();
            snapStream.close();
            br.close();
            outStream = null;
            snapStream = null;
            br = null;
            Integer rtn =  process.waitFor();

            if(rtnCodeStr == null){
                return rtn;
            }else{
                return Integer.valueOf(StringUtils.substringAfter(rtnCodeStr, CoreConst.regex).trim());
            }
        }catch(Exception e){
            logger.error(inst.getTaskId()+"("+inst.getTaskName()+") execute wormhole command error",e);
            return CoreConst.INTERNAL_EXECUTE_ERROR;
        }finally{
            StreamWriter.cleanup(outStream,snapStream,br);
        }
    }

    public static Integer executeCommand(InstanceDO inst){
        String cmdLine = StringUtils.join(new String[]{inst.getTaskObj(),inst.getPara1(),inst.getPara2(),inst.getPara3()}," ");
        logger.debug("cmdline := "+cmdLine);
        try{
            Process process = Runtime.getRuntime().exec(cmdLine);
            File outputFile = new File(inst.getLogPath());
            File snapshotOutputFile = new File(StringUtils.join(new String[]{inst.getLogPath(), "snapshot"}, "."));
            StreamWriter outPrinter = new StreamWriter(process.getInputStream(), outputFile,snapshotOutputFile);
            StreamWriter errPrinter = new StreamWriter(process.getErrorStream(), outputFile,snapshotOutputFile);
            outPrinter.start();
            errPrinter.start();
            return process.waitFor();
        }catch(Exception e){
            logger.error(inst.getTaskId()+"("+inst.getTaskName()+") execute command error",e);
            return CoreConst.INTERNAL_EXECUTE_ERROR;
        }
    }
}
