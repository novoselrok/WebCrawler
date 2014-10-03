package com.redditprog.webcrawler;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
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
     * @param sub SubReddit name
     * @param num Number of pictures to extract
     * @param dir Directory path to save photos
     * @param type_of_links Reddit's links categories
     * @param top_time Range of Date/Time for the subreddit
     * @param scanner Scanner object pass from Launcher
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
                json_url = GlobalConfiguration.REDDIT_PRE_SUB_URL + this.sub + "/"
                        + this.type_of_links + "/.json?sort=top&t="
                        + this.top_time;
            } else {
                json_url = GlobalConfiguration.REDDIT_PRE_SUB_URL + this.sub + "/"
                        + this.type_of_links + "/.json";
            }
            urlJson = new URL(json_url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        JSONObject obj;
        JSONArray childArray;
        int count = GlobalConfiguration.TOTAL_ITEMS_PER_PAGE;

        while (true) {

            String jsonString = this.extractJsonFromUrl(urlJson);

            try {
                obj = new JSONObject(jsonString);
                String after = obj.getJSONObject("data").getString("after");
                childArray = obj.getJSONObject("data").getJSONArray("children");

                for (int i = 0; i < childArray.length(); i++) {
                    String urlString = this.getImageURL(childArray, i);
                    URL url = new URL(urlString);

                    if (urlString.contains("imgur")) {
                        if (urlString.contains(GlobalConfiguration.IMGUR_ALBUM_URL_PATTERN)) {
                            numDownloads = this.extractImgurAlbum(numDownloads, url);
                        } else {
                            if (urlString.contains(GlobalConfiguration.IMGUR_SINGLE_URL_PATTERN)) {
                                url = new URL(urlString);
                            } else {
                                urlString = urlString.replace("imgur", "i.imgur");
                                url = new URL(urlString + ".gif");
                            }
                            numDownloads = this.extractSingle(numDownloads, url, "single");
                        }
                    } else {
                        if (urlString.endsWith("jpg")
                                || urlString.endsWith("png")
                                || urlString.endsWith("jpeg")) {
                            numDownloads = this.extractSingle(numDownloads, url, "single");
                        }
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
                urlJson = new URL(json_url);
                count += GlobalConfiguration.TOTAL_ITEMS_PER_PAGE;

                if (count >= GlobalConfiguration.TOTAL_SEARCH_LIMIT
                        && numDownloads < this.num_pics) {
                    System.out.println(GlobalConfiguration.RESPONSE_RESULT_FAIL);
                    break;
                }
            } catch (JSONException ex) {
                Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MalformedURLException ex) {
                Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (numDownloads >= this.num_pics) {
                this.askUserToOpenFolder();
                break;
            }
        }
    }

    private String getImageURL(JSONArray childArray, int iteration) {
        String urlString = null;
        try {
            if (childArray.getJSONObject(iteration).getJSONObject("data").has("url")) {
                urlString = childArray.getJSONObject(iteration).getJSONObject("data").getString("url");
            } else if (childArray.getJSONObject(iteration).getJSONObject("data").has("link_url")) {
                // Gilded range change the Data's url child to link_url
                urlString = childArray.getJSONObject(iteration).getJSONObject("data").getString("link_url");
            } else {
                urlString = "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return urlString;
    }

    private int extractSingle(int numDownloads, URL url, String new_map) {
        String fileName = url.getFile();
        // + 1 because this.dir already got "/" as the last character
        String destName;
        if (new_map.equals("single")) {
            destName = this.dir + fileName.substring(fileName.lastIndexOf("/") + 1);
        } else {
            destName = this.dir + new_map + File.separator + fileName.substring(fileName.lastIndexOf("/") + 1);
        }

        if (destName.contains("?")) {
            destName = destName.substring(0, destName.length() - 2);
        }

        if(imageIsDuplicate(destName,url)) return numDownloads + 1;
        
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
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null,
                    ex);
            // Invalid file save needs to terminate the application, 
            // saving the next image will result to the same error
            // Value of 1 (argument) or any > 0 number means building error has occured
            System.exit(1);
        } catch (IOException exc)  {
            Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null,
                    exc);
            return  numDownloads;
        }
        return numDownloads + 1;
    }

	private int extractImgurAlbum(int numDownloads, URL url) {
        String Client_ID = ClientIDClass.CLIENT_ID;

        String[] urlSplit = url.toString().split("/");
        String url_s = urlSplit[urlSplit.length - 1];
        JSONObject obj;
        JSONArray images_array;

        try {
            URL jsonUrl = new URL(GlobalConfiguration.IMGUR_API_URL + url_s);

            HttpURLConnection conn = (HttpURLConnection) jsonUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Client-ID " + Client_ID);

            BufferedReader bin = new BufferedReader(new InputStreamReader(conn.getInputStream()));;
            StringBuilder jsonString = new StringBuilder();

            String line;
            while ((line = bin.readLine()) != null) {
                jsonString.append(line);
            }

            bin.close();

            obj = new JSONObject(jsonString.toString());

            images_array = obj.getJSONObject("data").getJSONArray("images");
            String album_title = obj.getJSONObject("data").getString("title");
            int album_num_pics = obj.getJSONObject("data").getInt("images_count");

            System.out.println("An album detected! Title is: " + album_title + " Number of pics: " + album_num_pics);
            System.out.println(GlobalConfiguration.QUESTION_ALBUM_DOWNLOAD);

            String response = "";
            while (true) {
                response = scanner.next();
                if (response.equals("y") || response.equals("yes")
                        || response.equals("n") || response.equals("no")) {
                    break;
                }
            }
            if (response.equals("y") || response.equals("yes")) {
                new File(this.dir + url_s + File.separator).mkdir();
                for (int i = 0; i < images_array.length(); i++) {
                    numDownloads = extractSingle(numDownloads, new URL(
                            images_array.getJSONObject(i).getString("link")),
                            url_s);
                }
            } else {
                return numDownloads;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return numDownloads;
    }

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
            reader.close();
            return buffer.toString();
        } catch (IOException ex) {
            Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    private void openFolder() {
        try {
            Desktop.getDesktop().open(new File(this.dir));
        } catch (IOException e) {
            System.out.println(GlobalConfiguration.INVALID_RESPONSE_INVALID_FOLDER);
            e.printStackTrace();
        }
    }


    /**
     * Function that makes sure to avoid downloading identical files. If a conflict is found asks the user to manually solve it.
     * 
     * @param destName the file destionation path
     * @param url image source
     * @return if the image download has to be skipped (true)
     */
    private boolean imageIsDuplicate(String destName, URL url) {
		File file = new File(destName);
		boolean isImgurLink = url.toString().contains(GlobalConfiguration.IMGUR_CHECK_STRING);
		//The file exists and it's being downloaded from imgur so its ID is unique -> It's a duplicate.
		if(file.exists() && isImgurLink) return true;
		else if (file.exists() && !isImgurLink){
	        System.out.println(url + " --> " + GlobalConfiguration.FILE_ALREADY_EXISTS_DIALOG);
			//Asking user if he wants to overwrite.
	        while (true) {
	            String openFolder = this.scanner.next();
	            if (openFolder.equalsIgnoreCase("y") || openFolder.equalsIgnoreCase("yes")) {
	                return false; //Overwrite it
	            } else if (openFolder.equalsIgnoreCase("n") || openFolder.equalsIgnoreCase("no")) {
	                return true;
	            } else {
	                System.out.println("Enter y or n");
	            }
	        }
		}
		//File doesn't exist.
		return false;
	}
    
    private void askUserToOpenFolder() {
        System.out.println(GlobalConfiguration.RESPONSE_RESULT_SUCCESS);
        System.out.println("Do you want to open " + this.dir + "in your File Explorer? (y/n)");
        boolean isSelected = false;
        while (!isSelected) {
            String openFolder = this.scanner.next();
            if (openFolder.equalsIgnoreCase("y") || openFolder.equalsIgnoreCase("yes")) {
                this.openFolder();
                isSelected = true;
            } else if (openFolder.equalsIgnoreCase("n") || openFolder.equalsIgnoreCase("no")) {
                isSelected = true;
            } else {
                System.out.println("Enter y or n");
            }
        }
    }
}
