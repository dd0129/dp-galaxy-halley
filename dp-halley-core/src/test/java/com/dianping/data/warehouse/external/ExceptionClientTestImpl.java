package com.dianping.data.warehouse.external;

import com.dianping.data.warehouse.halley.client.HalleyClient;

import java.util.Map;

/**
 * Created by hongdi.tang on 14-4-4.
 */
public class ExceptionClientTestImpl implements HalleyClient {
    @Override
    public String run(Map<String,String> cmds) {
        System.out.println("test.........");
        return "{code:501,message:\"success\"}";
    }
}
