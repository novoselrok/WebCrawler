/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redditprog.webcrawler;

import java.io.InputStream;
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
            final URL url = new URL("http://reddit.com/r/" + sub);

            // create a new connection
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            
            // connects to the http
            huc.connect();

            // Possible solution (do not delete)
            //System.out.println("connected url: " + huc.getURL());

            // Extract the redirect url in string
            InputStream is = huc.getInputStream();
            String redirectURL = huc.getURL().getPath();
            is.close();
            
            // checks if it is a redirect and return boolean value
            return redirectURL.contains("/r/");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

}
