package com.dreamfish.fishblog.core.utils.id;

import java.util.UUID;

public class UuidUtils {

    /**
     * 生成不带 ”-“ 的 UUID
     * @return
     */
    public static String careateUuid(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
}
