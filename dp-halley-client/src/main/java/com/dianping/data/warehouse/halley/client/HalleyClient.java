package com.dianping.data.warehouse.halley.client;

import java.util.Map;

/**
 * Created by hongdi.tang on 14-4-3.
 */
public interface HalleyClient {
    public String run(Map<String,String> cmds);
}
