package com.redditprog.webcrawler;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Ryan
 * @author Rok
 */
public class Extractor {

	// Instance variables
	private final int num_pics;
	private final String sub;
	private final String dir;
	private final String type_of_links;
	private final String top_time;
	private final Scanner scanner;

	/**
	 * The class constructor with the following parameters:
	 *
	 * @param sub
	 *            SubReddit name
	 * @param num
	 *            Number of pictures to extract
	 * @param dir
	 *            Directory path to save photos
	 * @param top_time
	 *            Range of Date/Time for the subreddit
	 * @param scanner
	 *            Scanner object pass from Launcher
	 */
	public Extractor(String sub, int num, String dir, String type_of_links,
			String top_time, Scanner scanner) {
		this.sub = sub;
		this.num_pics = num;
		this.dir = dir;
		this.type_of_links = type_of_links;
		this.top_time = top_time;
		this.scanner = scanner;

	}

	public void beginExtract() {

		// set the full url of the user input subreddit
		URL urlJson;
		String json_url;
		int numDownloads = 0;
		try {
			// urlSub = new URL("http://reddit.com/r/" + this.sub);
			// http://www.reddit.com/r/Gunners/top/.json?sort=top&t=month
			if (type_of_links.equals("top")) {
				json_url = "http://www.reddit.com/r/" + this.sub + "/"
						+ this.type_of_links + "/.json?sort=top&t="
						+ this.top_time;
			} else {
				json_url = "http://www.reddit.com/r/" + this.sub + "/"
						+ this.type_of_links + "/.json";

			}
			urlJson = new URL(json_url);
		} catch (MalformedURLException ex) {
			Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null,
					ex);
			return;
		}

		JSONObject obj;
		JSONArray childArray;
		int count = 25;

		while (true) {

			String jsonString = this.extractJsonFromUrl(urlJson);

			try {
				obj = new JSONObject(jsonString);
				String after = obj.getJSONObject("data").getString("after");
				childArray = obj.getJSONObject("data").getJSONArray("children");

				// mainloop:
				for (int i = 0; i < childArray.length(); i++) {
					String urlString = "";

					if (childArray.getJSONObject(i).getJSONObject("data")
							.has("url")) {
						urlString = childArray.getJSONObject(i)
								.getJSONObject("data").getString("url");
					} else {
						// Gilded range change the Data's url child to link_url
						if (childArray.getJSONObject(i).getJSONObject("data")
								.has("link_url")) {
							urlString = childArray.getJSONObject(i)
									.getJSONObject("data")
									.getString("link_url");
						}
					}

					URL url;
					if (urlString.contains("imgur.com/a/")) {
						url = new URL(urlString);
						numDownloads = this
								.extractImgurAlbum(numDownloads, url);
					} else {

						if (urlString.contains("i.imgur.com")) {
							url = new URL(urlString);
						} else {
							urlString = urlString.replace("imgur", "i.imgur");
							System.out.println(urlString);
							url = new URL(urlString + ".png");
						}

						numDownloads = this.extractImgurSingle(numDownloads,
								url);
					}
					if (numDownloads >= this.num_pics) {
						break;
					}

				}

				if (type_of_links.equals("top")) {
					json_url += "&count=" + count + "&after=" + after;
				} else {
					json_url += "?count=" + count + "&after=" + after;
				}
				System.out.println("JSONURL= " + json_url + "NUMDOWNLOADS= "
						+ numDownloads);
				urlJson = new URL(json_url);
				count += 25;
			} catch (JSONException ex) {
				Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE,
						null, ex);
			} catch (MalformedURLException ex) {
				Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE,
						null, ex);
			}

			if (this.num_pics <= numDownloads) {
				this.askUserToOpenFolder();
				break;
			}

		}
	}

	private int extractImgurSingle(int numDownloads, URL url) {

		String fileName = url.getFile();
		// + 1 because this.dir already got "/" as the last character
		String destName = this.dir
				+ fileName.substring(fileName.lastIndexOf("/") + 1);

		InputStream is;
		OutputStream os;
		try {
			is = url.openStream();
			os = new FileOutputStream(destName);
			byte[] b = new byte[2048];
			int length;

			while ((length = is.read(b)) != -1) {
				os.write(b, 0, length);
			}

			is.close();
			os.close();

			this.printDownloadCompleted(numDownloads, destName);
			return numDownloads + 1;
		} catch (IOException ex) {
			Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null,
					ex);
			return numDownloads;
		}

	}

	private int extractImgurAlbum(int numDownloads, URL url) {
		String Client_ID = "6e4aad85ecee137";
		String Client_Secret = "9f6c50ff4fca8f076a83db05fc4799e84e4e8c3b";
		String[] url_s = url.toString().split("/");

		JSONObject obj;
		JSONArray images_array;
		try {

			URL jsonUrl = new URL("https://api.imgur.com/3/album/"
					+ url_s[url_s.length - 1]);

			HttpURLConnection conn = (HttpURLConnection) jsonUrl
					.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Authorization", "Client-ID " + Client_ID);

			BufferedReader bin = null;
			bin = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuilder jsonString = new StringBuilder();
			// below will print out bin
			String line;
			while ((line = bin.readLine()) != null)
				jsonString.append(line);

			bin.close();

			obj = new JSONObject(jsonString.toString());
			images_array = obj.getJSONObject("data").getJSONArray("images");

			for (int i = 0; i < images_array.length(); i++) {
				numDownloads = extractImgurSingle(numDownloads, new URL(
						images_array.getJSONObject(i).getString("link")));
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return numDownloads;
	}

	/*
	 * private void filterPhoto() {
	 * 
	 * }
	 */
	private void printDownloadCompleted(int num, String path) {
		System.out.println("==================");
		System.out.println("Download #" + (num + 1) + " complete:");
		System.out.println("Name of the file: " + path);
	}

	private String extractJsonFromUrl(URL url) {
		BufferedReader reader = null;
		try {

			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[1024];
			while ((read = reader.read(chars)) != -1) {
				buffer.append(chars, 0, read);
			}

			return buffer.toString();

		} catch (IOException ex) {
			Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null,
					ex);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
					Logger.getLogger(Extractor.class.getName()).log(
							Level.SEVERE, null, ex);
				}
			}
		}

		return "";
	}

	private void openFolder() {
		try {
			Desktop.getDesktop().open(new File(this.dir));
		} catch (IOException e) {
			System.out
					.println("Ooops, looks like this folder doesn't exist :(");
			e.printStackTrace();
		}
	}

	private void askUserToOpenFolder() {
		System.out.println("==================");
		System.out.println("Download finished!");
		System.out.println("==================");
		System.out.println("Do you want to open " + this.dir
				+ "\nin your File Explorer? (y/n)");
		boolean isSelected = false;
		while (!isSelected) {
			String openFolder = this.scanner.next();
			if (openFolder.equalsIgnoreCase("y")
					|| openFolder.equalsIgnoreCase("yes")) {
				this.openFolder();
				isSelected = true;
			} else if (openFolder.equalsIgnoreCase("n")
					|| openFolder.equalsIgnoreCase("no")) {
				isSelected = true;
			} else {
				System.out.println("Enter y or n");
			}
		}
	}

}
