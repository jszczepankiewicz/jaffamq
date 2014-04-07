package org.jaffamq.test;

import com.jayway.jsonpath.JsonPath;

import java.util.List;

/**
 * Created by urwisy on 05.04.14.
 */
public class JsonPathScalaAdapter {

    public static List<Object> read(String json, String jsonPath) {
        return JsonPath.read(json, jsonPath);
    }

    public static Object readObject(String json, String jsonPath){
        return JsonPath.read(json, jsonPath);
    }
}
