/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ardz.bandwidthcontroller;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

/**
 *
 * @author ardzix
 */
public class LinuxCommand {
    static InputStream is;
    static byte[] buff = new byte[8192];
    static int n;
    
    public String getPasswdForRoot() throws IOException {
        Process p = Runtime.getRuntime().exec(new String[]{"sh","-c","sudo -S id"});
        is = p.getErrorStream();
        n = is.read(buff, 0, 8192);
        String text = new String(buff,0,n);
        if(text.contains("root"))return null; //not set password
        JPanel panel = new JPanel(new BorderLayout());
        JLabel lab = new JLabel(text);
        panel.add(lab,BorderLayout.NORTH);
        JPasswordField password = new JPasswordField();
        panel.add(password,BorderLayout.SOUTH);
        JOptionPane.showMessageDialog(null, panel);
        byte[] passwd = (new String(password.getPassword())+"\r\n").getBytes();
        p.getOutputStream().write(passwd);
        p.getOutputStream().flush();
        n = is.read(buff, 0, 8192);
        if(n==-1) return new String(password.getPassword());
        text = new String(buff,0,n);
        while(true) {
            lab.setText(text);
            JOptionPane.showMessageDialog(null, panel);
            p = Runtime.getRuntime().exec(new String[]{"sh","-c","sudo -S id"});
            is = p.getErrorStream();
            n = is.read(buff, 0, 8192);
            passwd = (new String(password.getPassword())+"\n").getBytes();
            p.getOutputStream().write(passwd);
            p.getOutputStream().flush();
            n = is.read(buff, 0, 8192);
            if(n==-1) return new String(password.getPassword());
            text = new String(buff,0,n);
        }
    }
    
    public Process runFromRoot(String command, String password) throws IOException {
        byte[] passwd = (password+"\n").getBytes(); //for OutputStream better is byte[]
        Process p = Runtime.getRuntime().exec(new String[]{"sh","-c","sudo -S "+command});
        p.getOutputStream().write(passwd);
        p.getOutputStream().flush();
        return p;
    }
    
    
    public String streamToString(InputStream stream) {
    String read = "";
    try {
        while((n=stream.read(buff, 0, 8192))!=-1) {
            read+=new String(buff,0,n);
        }
    } catch (IOException e) {
    }
    return read;
    }
}
