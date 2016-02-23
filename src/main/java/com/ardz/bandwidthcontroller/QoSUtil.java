/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ardz.bandwidthcontroller;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

/**
 *
 * @author ardzix
 */
public class QoSUtil {
    
    JSONObject qosList;
    HttpClientHandler http = new HttpClientHandler();
    
    

    public String[] getQosListStringArray(String url) {
        String[] qosListName = null;
        try {
            qosList = http.sendGet(url);
            this.qosList = qosList;
            qosListName = getQoSStringArray("name");
            
        } catch (Exception ex) {
            Logger.getLogger(PolicyConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return qosListName;

    }
    public JSONObject getQosList() {
        return this.qosList;

    }
    
    
    private String[] getQoSStringArray(String key) throws Exception {

        int switchTotal = this.qosList.size();
        String[] QoS = new String[switchTotal];
        for (int i = 0; i < switchTotal; i++) {
            JSONObject currentSwitchList = (JSONObject) this.qosList.get(i);
            QoS[i] = (String) currentSwitchList.get(key);
        }
        return QoS;
    }

    
}
