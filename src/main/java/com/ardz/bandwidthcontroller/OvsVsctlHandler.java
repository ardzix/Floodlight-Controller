/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ardz.bandwidthcontroller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

/**
 *
 * @author ardzix
 */
public final class OvsVsctlHandler {

    JSONObject bridges = new JSONObject();
    JSONObject queues = new JSONObject();
    int defaultMaxBandwidth = 10000000;

    public OvsVsctlHandler() {
        addQueues(20000000);
        try {
            getOVSTopology();
        } catch (IOException ex) {
            Logger.getLogger(OvsVsctlHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        OvsVsctlHandler o = new OvsVsctlHandler();
        System.out.println(o.getBridges().toString());
//        try {
//            o.executeCommand(o.setCommand(6));
//        } catch (IOException ex) {
//            Logger.getLogger(OvsVsctlHandler.class.getName()).log(Level.SEVERE, null, ex);
//        }

    }

    public void setDefaultMaxBandwidth(int maxBandwidt) {
        this.defaultMaxBandwidth = maxBandwidt;
    }

    public int getDefaultMaxBandwidth() {
        return this.defaultMaxBandwidth;
    }

    public String[] getBridgesStringArray() {
        JSONObject localBridges = this.bridges;
        int localBridgesSize = localBridges.size();
        String[] bridgesInStringArray = new String[localBridgesSize] ;
        for(int i=1;i<=localBridgesSize;i++){
            JSONObject localBridge = (JSONObject) localBridges.get(i);
            bridgesInStringArray[i-1] = localBridge.get("bridge").toString();
        }
        return bridgesInStringArray;
    }

    public JSONObject getBridges() {
        return this.bridges;
    }

    public String[] getQueuesStringArray() {
        JSONObject localQueues = this.queues;
        int queuesSize = localQueues.size();
        String[] queuesInStringArray = new String[queuesSize+1];
        queuesInStringArray[0]=String.valueOf(this.defaultMaxBandwidth);
        for(int i=1;i<=queuesSize;i++){
            queuesInStringArray[i]=localQueues.get(i).toString();
        }
        return queuesInStringArray;
    }

    public JSONObject getQueues() {
        return this.queues;
    }

    public void addQueues(int maxBandwidth) {
        JSONObject queue = this.queues;
        int size;
        size = queue.size();
        queue.put(size + 1, maxBandwidth);
        this.queues = queue;
    }

    public void removeQueues(int id) {
        JSONObject queue = this.queues;
        int size;
        size = queue.size();
        queue.remove(id);
        queue.put(id, queue.get(id + 1));
        for (int i = id + 1; i < size; i++) {
            queue.replace(i, queue.get(i + 1));
        }
        queue.remove(size);
        this.queues = queue;
    }

    public void replaceQueues(int id, int maxBandwidth) {
        JSONObject queue = this.queues;
        queue.replace(id, maxBandwidth);
        this.queues = queue;
    }

    public String setCommand(int bridgeNo) {

        String queues = "queues=";
        String idQueues = "";
        int queueSize = this.queues.size();
        JSONObject bridge = (JSONObject) this.bridges.get(bridgeNo);
        System.out.println("Switch : " + bridge.get("bridge"));
        JSONObject ports = (JSONObject) bridge.get("ports");
        String portsCommand = "";
        for (int i = 0; i < ports.size()-1; i++) {
            portsCommand = portsCommand +"-- set port "+ports.get(i+1)+" qos=@defaultqos ";
        }

        for (int i = 0; i < queueSize; i++) {
            queues = queues + i + "=@q" + i;
            idQueues = idQueues + "--id=@q" + i
                    + " create queue other-config:max-rate="
                    + this.queues.get(i + 1);
            if (i != queueSize - 1) {
                queues = queues + ",";
                idQueues = idQueues + " -- ";
            } else {
                queues = queues + " -- ";
            }
        }

        String command = "ovs-vsctl "
                + portsCommand
                + "-- "
                + "--id=@defaultqos create qos "
                + "type=linux-htb other-config:max-rate=" + this.defaultMaxBandwidth + " "
                + queues
                + idQueues;
        System.out.println(command);
        return command;
    }
    
    public String cleanQoS(){
        return "ovs-vsctl --all destroy qos";
    }
    public String cleanQueue(){
        return "ovs-vsctl --all destroy queue";
    }

    public String executeCommand(String command) throws IOException {
        /* first edit /etc/sudoers
         * and add "admin    ALL = NOPASSWD: ALL"
         *
         */
        LinuxCommand lc = new LinuxCommand();
        String password = lc.getPasswdForRoot();
        Process p = lc.runFromRoot(command, password);
        return lc.streamToString(p.getInputStream());

    }

    private JSONObject findBridge(String b) {
        char[] charArray = b.toCharArray();
        JSONObject bridge1 = new JSONObject();
        int charArrayLength = charArray.length;
        int bridgeNo = 1;
        for (int i = 0; i < charArrayLength; i++) {
            if (charArray[i] == (char) 'B'
                    && charArray[i + 1] == (char) 'r'
                    && charArray[i + 2] == (char) 'i'
                    && charArray[i + 3] == (char) 'd'
                    && charArray[i + 4] == (char) 'g'
                    && charArray[i + 5] == (char) 'e') {

                JSONObject bridgeDetail = new JSONObject();
                String bridgeName = "";
                int j = 8;
                while (charArray[i + j] != (char) '\"') {
                    bridgeName = bridgeName + charArray[i + j];
                    j++;
                }

                bridgeDetail.put("name", bridgeName);
                bridgeDetail.put("startDtl", i + j + 1);
                bridge1.put(bridgeNo, bridgeDetail);
                bridgeNo++;
            }
        }
        JSONObject bridge2 = new JSONObject();
        for (int i = 1; i < bridgeNo; i++) {
            JSONObject bridgeTemp = (JSONObject) bridge1.get(i);
            JSONObject bridgeNext = new JSONObject();
            if (i != bridgeNo - 1) {
                bridgeNext = (JSONObject) bridge1.get(i + 1);
            }
            JSONObject bridgeDetail = new JSONObject();
            bridgeDetail.put("startDtl", bridgeTemp.get("startDtl"));
            if (i != bridgeNo - 1) {
                bridgeDetail.put("endDtl", bridgeNext.get("startDtl"));
            }
            bridgeDetail.put("name", bridgeTemp.get("name"));
            bridge2.put(i, bridgeDetail);
        }

        JSONObject bridge3 = new JSONObject();
        for (int i = 1; i < bridgeNo; i++) {
            JSONObject bridgeTemp = (JSONObject) bridge2.get(i);
            JSONObject bridgeDetail = new JSONObject();
            String details = "";
            int endCharCOunt;
            if (i != bridgeNo - 1) {
                endCharCOunt = (int) bridgeTemp.get("endDtl");
            } else {
                endCharCOunt = charArrayLength;
            }
            for (int j = (int) bridgeTemp.get("startDtl"); j < endCharCOunt; j++) {
                details = details + charArray[j];
            }
            bridgeDetail.putAll(bridgeTemp);
            bridgeDetail.put("details", details);
            bridge3.put(i, bridgeDetail);
        }

        return bridge3;
    }

    public void getOVSTopology() throws IOException {

        String output;
        output = executeCommand("ovs-vsctl show");
        JSONObject bridgeDetail = findBridge(output);
        JSONObject bridgePort;
        bridgePort = new JSONObject();
        int bridgeNo = 1;
        while (bridgeDetail.get(bridgeNo) != null) {
            JSONObject bridgeTemp = (JSONObject) bridgeDetail.get(bridgeNo);
            String bridgeDetails = (String) bridgeTemp.get("details");
            JSONObject port = new JSONObject();
            port.put("bridge", bridgeTemp.get("name"));
            port.put("ports", findPort(bridgeDetails));
            bridgePort.put(bridgeNo, port);
            bridgeNo++;
        }
        this.bridges = bridgePort;
    }

    private JSONObject findPort(String details) {

        char[] charArray = details.toCharArray();
        JSONObject port = new JSONObject();
        int charArrayLength = charArray.length;
        int portNo = 1;
        for (int i = 0; i < charArrayLength; i++) {
            if (charArray[i] == (char) 'P'
                    && charArray[i + 1] == (char) 'o'
                    && charArray[i + 2] == (char) 'r'
                    && charArray[i + 3] == (char) 't') {

                String portName = "";
                int j = 6;
                while (charArray[i + j] != (char) '\"') {
                    portName = portName + charArray[i + j];
                    j++;
                }
                port.put(portNo, portName);
                portNo++;
            }
        }
        return port;

    }

}
