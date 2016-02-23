/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ardz.bandwidthcontroller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ardzix
 */
public class Util {

    static Properties prop = new Properties();
    static InputStream input = null;
    static OutputStream output = null;
    static String controllerAPIUrl = null;
    static String controllerAPIPort = null;

    public static void setProperty(String key, String value) {
        try {
            output = new FileOutputStream("config.properties");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {

            // set the properties value
            prop.setProperty(key, value);

            // save properties to project root folder
            prop.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static void getProperty() {
        try {

            input = new FileInputStream("config.properties");

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            Util.controllerAPIUrl = prop.getProperty("controllerAPIUrl");
            Util.controllerAPIPort = prop.getProperty("controllerAPIPort");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getControllerAddres() {
        return Util.controllerAPIUrl + ":" + controllerAPIPort;
    }

}
