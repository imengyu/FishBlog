package com.dreamfish.fishblog.core.utils.json;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;

public class JsonUtils {

    public static String JsonValueToString(JSONObject object, String keyName){
        String data;
        Object keyObject = object.get(keyName);
        if(keyObject instanceof JSONObject) data = object.getJSONObject(keyName).toJSONString();
        else if(keyObject instanceof String) data = "'" + object.getString(keyName) + "'";
        else if(keyObject instanceof JSONArray) data = object.getJSONArray(keyName).toJSONString();
        else if(keyObject instanceof ArrayList) data = object.getJSONArray(keyName).toJSONString();
        else data = keyObject.toString();
        return data;
    }
}
