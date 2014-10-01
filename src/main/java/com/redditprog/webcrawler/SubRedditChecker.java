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

	public static boolean verifySubReddit(String sub) {
		int children_array_length = 0;
		try {
			// set the full url of the user input subreddit
			final URL url = new URL("http://reddit.com/r/" + sub + ".json");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			BufferedReader bin = null;
			bin = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuilder jsonString = new StringBuilder();

			// below will print out bin
			String line;
			while ((line = bin.readLine()) != null)
				jsonString.append(line);

			bin.close();
			JSONObject obj = new JSONObject(jsonString.toString());
			children_array_length = obj.getJSONObject("data")
					.getJSONArray("children").length();

		} catch (java.net.SocketTimeoutException e) {
			return false;
		} catch (java.io.IOException e) {
			return false;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return !(children_array_length == 0);
	}
}
