/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ardz.bandwidthcontroller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.springframework.util.FileCopyUtils;

/**
 *
 * @author ardzix
 */
public class HttpClientHandler {

    private final String USER_AGENT = "Ardz Bandwidth Controller/0.0.1";
    
    String currentStringResult = "";

    /**
     * Get JSON Object from url using HttpGet
     *
     * @author ardzix
     * @param url destination url
     * @return
     * @throws java.lang.Exception
     */
    public JSONObject sendGet(String url) throws Exception {

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);

        // add request header
        request.addHeader("User-Agent", USER_AGENT);

        HttpResponse response = client.execute(request);

        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : "
                + response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuilder result = new StringBuilder();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        String stringResult = result.toString();
        System.out.println("Response :\n"+stringResult);
        this.currentStringResult=stringResult;
        JSONHandler jsonObj = new JSONHandler();
        return jsonObj.jsonParser(stringResult);
    }
    
    public String getResponse(String url) throws Exception {

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);

        // add request header
        request.addHeader("User-Agent", USER_AGENT);

        HttpResponse response = client.execute(request);

        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : "
                + response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuilder result = new StringBuilder();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        String stringResult = result.toString();
        System.out.println("Response :\n"+stringResult);
        this.currentStringResult=stringResult;
        JSONHandler jsonObj = new JSONHandler();
        return stringResult;
    }
    
    public String getCurrentStringResult(){
        return this.currentStringResult;
    }

    /**
     * Post json object to url using HttpPost
     *
     * @author ardzix
     * @param url destination url
     * @param entity JSON object parsed to String
     * @return
     * @throws java.lang.Exception
     */
    public JSONObject sendPost(String url, String entity) throws Exception {

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        post.setHeader("Content-type", "application/json");

        StringEntity input = new StringEntity(entity);
        input.setContentType("application/json");
        post.setEntity(input);

        HttpResponse response = client.execute(post);
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + post.getEntity());
        System.out.println("Response Code : "
                + response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuilder result = new StringBuilder();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        String stringResult = result.toString();
        System.out.println("Response :\n"+stringResult);

        JSONHandler jsonObj = new JSONHandler();
        
        return jsonObj.jsonParser(stringResult);
    }
    
    /**
     * Post json object to url using HttpPost
     *
     * @author ardzix
     * @param urlString destination url
     * @param payload  JSON object parsed to String containing delete parameter
     * @return
     * @throws java.net.MalformedURLException
     */
    public JSONObject sendDelete(String urlString, String payload) throws MalformedURLException, IOException{
        
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestMethod("DELETE");
        urlConnection.setDoOutput(true);
        FileCopyUtils.copy(payload.getBytes(), urlConnection.getOutputStream());
        
        System.out.println("\nSending 'DELETE' request to URL : " + urlString);
        urlConnection.connect();
        System.out.println("Response Code : "
                + urlConnection.getResponseCode());
        
        final InputStream is = urlConnection.getInputStream();
           int b = is.read();
           String stringResult = "";
           while (b != -1) {
               stringResult += (char) b;
               b = is.read();
           }
           
        System.out.println("Response :\n"+stringResult);
        
        return null;
        
    }
}
