package org.zrclass.dto;

import java.util.HashMap;
import java.util.Map;

public class MapResult {
    static Map<String, Object> of(int code, String msg, Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        if (msg != null && !"".equals(msg)) {
            map.put("msg", msg);
        }
        if (data != null) {
            map.put("data", data);
        }

        return map;
    }

    public static Map<String, Object> ok() {
        return of(0, null, null);
    }

    public static Map<String, Object> ok(Object data) {
        return of(0, null, data);
    }

    public static Map<String, Object> fail() {
        return of(1, null, null);
    }

    public static Map<String, Object> fail(String msg) {
        return of(1, msg, null);
    }

}
