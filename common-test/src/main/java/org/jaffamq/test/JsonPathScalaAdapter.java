package org.jaffamq.test;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.List;

/**
 * Created by urwisy on 05.04.14.
 */
public class JsonPathScalaAdapter {

    public static List<Object> read(String json, String jsonPath) {
        return JsonPath.read(json, jsonPath);
    }

    public static JSONObject readObject(String json, String jsonPath){
        Object x = JsonPath.read(json, jsonPath);

        if(x instanceof JSONArray){

            JSONArray ret = (JSONArray)x;

            if(ret.size() == 0){
                throw new IllegalStateException("Expected size 1 but was: " + ret.size());
            }

            Object obj = ret.iterator().next();

            if(obj instanceof JSONObject){
                return (JSONObject)obj;
            }
            else{
                throw new IllegalStateException("Expected type JSONObject but not found");
            }

        }

        throw new IllegalStateException("Expected type JSONArray but not found");
    }
}
