/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ardz.bandwidthcontroller;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author ardzix
 */
public class JSONHandler {

    /**
     * Convert JSONString to JSONObject
     * @author ardzix
     * @param stringJSON JSON object parsed to String
     * @return JSON object
     * @throws org.json.simple.parser.ParseException
     */
    public JSONObject jsonParser(String stringJSON) throws ParseException {

//      Dummy json object
        JSONObject obj = new JSONObject();

        JSONParser parser = new JSONParser();
        Object resultObject = parser.parse(stringJSON);
        if (resultObject instanceof JSONArray) {
            JSONArray array = (JSONArray) resultObject;
            for (Object object : array) {
                obj.put(array.indexOf((JSONObject) object),(JSONObject) object);
            }
        } else if (resultObject instanceof JSONObject) {
            obj = (JSONObject) resultObject;
        }

        return obj;
    }
}
