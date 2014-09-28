package com.redditprog.webcrawler;


import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Rok
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
            huc.setRequestMethod("HEAD");

            // set timeout 5 sec
            huc.setConnectTimeout(5000);

            return (huc.getResponseCode() == HttpURLConnection.HTTP_OK);

        } catch (java.net.SocketTimeoutException e) {
            return false;
        } catch (java.io.IOException e) {
            return false;
        }
    }
}
