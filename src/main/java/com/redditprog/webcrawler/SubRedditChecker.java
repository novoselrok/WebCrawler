package com.redditprog.webcrawler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Rok
 * @author ryan
 */
public class SubRedditChecker {
    private static final int CODE_OK = 1;
    private static final int CODE_BUSY = 2;
    private static final int CODE_INVALID = 0;
    
    public static int verifySubReddit(String sub) {
        int children_array_length = 0;
        try {
            // set the full url of the user input subreddit
            final URL url = new URL(GlobalConfiguration.REDDIT_PRE_SUB_URL + sub + ".json");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            BufferedReader bin = null;
            bin = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder jsonString = new StringBuilder();

            // below will print out bin
            String line;
            while ((line = bin.readLine()) != null) {
                jsonString.append(line);
            }

            bin.close();
            
            // For debugging, for inspection of the contents
            // Delete after testing
            System.out.println(jsonString);
            
            if (!jsonString.toString().startsWith(GlobalConfiguration.REDDIT_JSON_PATTERN)) {
                return CODE_BUSY;
            }
            
            JSONObject obj = new JSONObject(jsonString.toString());
            children_array_length = obj.getJSONObject("data").getJSONArray("children").length();
            
            if (children_array_length > 0) {
                return CODE_OK;
            } else {
                return CODE_INVALID;
            }
            
            
        } catch (java.net.SocketTimeoutException e) {
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Return value for exceptions
        return CODE_INVALID;
    }
}
