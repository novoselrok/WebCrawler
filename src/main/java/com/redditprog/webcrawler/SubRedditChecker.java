/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redditprog.webcrawler;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author ryan
 */
public class SubRedditChecker {
    public static boolean verifySubReddit(String sub) {
        
        try {
            // set the full url of the user input subreddit
            final URL url =  new URL("http://reddit.com/r/" + sub);
            
            // create a new connection
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            
            // disable any redirect, since reddit automatically use search if
            // subreddit does not exist
            huc.setInstanceFollowRedirects(false);
            
            // Uses HEAD as request for faster link check
            huc.setRequestMethod("HEAD");
            
            return (huc.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
    }
    
}
