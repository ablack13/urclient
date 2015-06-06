package com.scijoker.urclient;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by scijoker on 08.12.14.
 */
class DefaultClientHelper {
    public static Map getDefaultHeaders() {
        Map map = new HashMap();
        map.put("Accept", "application/json");
        return map;
    }
}
