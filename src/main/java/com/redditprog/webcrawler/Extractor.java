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
import java.util.ArrayList;
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
            String top_time) {
        this.sub = sub;
        this.num_pics = num;
        this.dir = dir;
        this.type_of_links = type_of_links;
        this.top_time = top_time;
    }

    public void beginExtract() {
        // set the full url of the user input subreddit
        URL urlJson;
        String json_url;
        int numDownloads = 0;
        ArrayList<String> gildedLinks = new ArrayList<String>();
        try {
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
        String base_url = json_url;

        while (true) {

            String jsonString = this.extractJsonFromUrl(urlJson);

            try {
                obj = new JSONObject(jsonString);
                String after = obj.getJSONObject("data").getString("after");
                if (after.equalsIgnoreCase("null") && json_url.contains("after")) {
                    System.out.println(GlobalConfiguration.NO_MORE_PICS_FOUND);
                    break;
                }
                childArray = obj.getJSONObject("data").getJSONArray("children");

                for (int i = 0; i < childArray.length(); i++) {
                    String urlString = this.getImageURL(childArray, i);
                    URL url = new URL(urlString);

                    // Skips duplicate child url links due to comments for Gilded
                    if (this.type_of_links.equals("gilded")) {
                        if (gildedLinks.contains(urlString)) {
                            continue;
                        } else {
                            gildedLinks.add(urlString);
                        }
                    }

                    if (urlString.contains("imgur")) {
                        if (urlString.contains(GlobalConfiguration.IMGUR_ALBUM_URL_PATTERN)) {
                            numDownloads = this.extractImgurAlbum(numDownloads, url);
                        } else if (urlString.contains(GlobalConfiguration.IMGUR_SINGLE_URL_PATTERN)) {
                            numDownloads = this.extractSingle(numDownloads, url, "single");
                        } else if (!isProperImageExtension(urlString)) {
                            String id = urlString.substring(urlString.lastIndexOf("/") + 1);
                            if (id.contains(",")) {
                                String[] arrayOfIds = id.split(",");
                                for (String arrayOfId : arrayOfIds) {
                                    numDownloads = extractPicFromImgurAPI(arrayOfId, numDownloads);
                                }
                            } else {
                                numDownloads = extractPicFromImgurAPI(id, numDownloads);
                            }
                        }
                    } else if (isProperImageExtension(urlString)) {
                        numDownloads = this.extractSingle(numDownloads, url, "single");
                    } else {
                        continue;
                    }

                    if (numDownloads >= this.num_pics) {
                        break;
                    }
                }

                if (type_of_links.equals("top")) {
                    json_url = base_url + "&count=" + count + "&after=" + after;
                } else {
                    json_url = base_url + "?count=" + count + "&after=" + after;
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
                break;
            }
        }
        if (numDownloads > 0) {
            this.askUserToOpenFolder();
        }
    }

    private int extractPicFromImgurAPI(String id, int numDownloads) {
        try {
            String extractedJson = extractImgurAPIJson(id, "image");

            // This might due to http error code connection
            // Error code results extractedJson to be empty
            // Skips extraction
            if (extractedJson.isEmpty()) {
                return numDownloads;
            }

            JSONObject object = new JSONObject(extractedJson);
            URL imageURL = new URL(object.getJSONObject("data").getString("link"));
            numDownloads = this.extractSingle(numDownloads, imageURL, "single");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return numDownloads;
    }

    private boolean isProperImageExtension(String image) {
        return image.endsWith("jpg") || image.endsWith("png") || image.endsWith("jpeg") || image.endsWith("gif");
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

        if (imageIsDuplicate(destName, url)) {
            return numDownloads;
        }

        // Verify http connection of the link
        int httpResponseCode = this.getResponseCode(url);

        if (httpResponseCode != 200) {
            return numDownloads;
        }

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
        } catch (IOException exc) {
            Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null,
                    exc);
            return numDownloads;
        }
        return numDownloads + 1;
    }

    private int extractImgurAlbum(int numDownloads, URL url) {
        String[] urlSplit = url.toString().split("/");
        String url_s = urlSplit[urlSplit.length - 1];
        JSONObject obj;
        JSONArray images_array;
        String extractedJson = extractImgurAPIJson(url_s, "album");

        // This might due to http error code connection
        // Skips album extraction
        if (extractedJson.isEmpty()) {
            return numDownloads;
        }

        try {
            obj = new JSONObject(extractedJson);

            images_array = obj.getJSONObject("data").getJSONArray("images");
            String album_title = obj.getJSONObject("data").getString("title");
            int album_num_pics = obj.getJSONObject("data").getInt("images_count");
            System.out.println("==================");
            System.out.println("An album detected! Title is: " + album_title + " Number of pics: " + album_num_pics);

            boolean isYes = InputValidator.getYesOrNoAnswer(GlobalConfiguration.QUESTION_ALBUM_DOWNLOAD);
            if (isYes) {
                // Filter duplicate album
                if (this.isAlbumDuplicate(url_s)) return numDownloads;
                
                new File(this.dir + url_s + File.separator).mkdir();
                for (int i = 0; i < images_array.length(); i++) {
                    numDownloads = extractSingle(numDownloads, new URL(images_array.getJSONObject(i).getString("link")), url_s);
                }
            } else {
                return numDownloads;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return numDownloads;
    }
    
    private boolean isAlbumDuplicate(String album_name) {
        File rootFolder = new File(this.dir);
        String[] items = rootFolder.list();
        
        for (String item : items) {
            if (new File(this.dir + item).isDirectory() && item.equalsIgnoreCase(album_name)) {
                System.out.println(GlobalConfiguration.ALBUM_ALREADY_EXISTS_NOTIFICATION);
                return true;
            }
        }
        
        return false;
    }

    private void printDownloadCompleted(int num, String path) {
        System.out.println("==================");
        System.out.println("Download #" + (num + 1) + " complete:");
        System.out.println("Name of the file: " + path);
    }

    // Authorisation is done twice, one for checking http connection through HEAD,
    // the other authorisation for json extraction though GET
    // Because authorisation is needed for every api service
    private String extractImgurAPIJson(String id, String apiType) {
        StringBuilder jsonString = new StringBuilder();
        URL jsonUrl;
        try {
            if (apiType.equals("album")) {
                jsonUrl = new URL(GlobalConfiguration.IMGUR_API_ALBUM_URL + id);
            } else if (apiType.equals("image")) {
                jsonUrl = new URL(GlobalConfiguration.IMGUR_API_IMAGE_URL + id);
            } else {
                return "";
            }

            // Creates new HttpUTLConnection via jsonURL
            HttpURLConnection conn = (HttpURLConnection) jsonUrl.openConnection();

            // Authorize connection first
            this.authorizeImgurConnection(conn);

            // Verify http connection of link
            // Some pictures are private in imgur
            int responseCode = this.getResponseCode(jsonUrl);
            if (responseCode != 200) {
                return "";
            }

            BufferedReader bin = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = bin.readLine()) != null) {
                jsonString.append(line);
            }

            bin.close();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return jsonString.toString();
    }

    private int getResponseCode(URL jsonUrl) {
        int responseCode = 0;
        try {
            HttpURLConnection aConnection = (HttpURLConnection) jsonUrl.openConnection();
            this.authorizeImgurConnection(aConnection);

            aConnection.setRequestMethod("HEAD");
            responseCode = aConnection.getResponseCode();

        } catch (IOException ex) {
            Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
        }

        return responseCode;

    }

    private void authorizeImgurConnection(HttpURLConnection aConnection) {
        boolean isAuthorised = false;
        try {
            aConnection.setRequestMethod("GET");
            aConnection.setRequestProperty("Authorization", "Client-ID " + ClientIDClass.CLIENT_ID);

            isAuthorised = true;
        } catch (ProtocolException ex) {
            Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Exits application if client id for imgur api is invalid
        if (!isAuthorised) {
            System.out.println(GlobalConfiguration.INVALID_CLIENT_ID_IMGUR_AUTHORIZATION);
            System.exit(1);
        }
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
     * Function that makes sure to avoid downloading identical files. If a
     * conflict is found asks the user to manually solve it.
     *
     * @param destName the file destination path
     * @param url image source
     * @return if the image download has to be skipped (true)
     */
    private boolean imageIsDuplicate(String destName, URL url) {
        File file = new File(destName);
        boolean isImgurLink = url.toString().contains(GlobalConfiguration.IMGUR_CHECK_STRING);
        //The file exists and it's being downloaded from imgur so its ID is unique -> It's a duplicate.
        if (file.exists() && isImgurLink) {
            System.out.println("==================\n" + url + " ---> " + GlobalConfiguration.FILE_ALREADY_EXISTS_NOTIFICATION);
            return true;
        } else if (file.exists() && !isImgurLink) {
            //Asking user if he wants to overwrite.
            return !InputValidator.getYesOrNoAnswer("==================\n" + url + " --> " + GlobalConfiguration.FILE_ALREADY_EXISTS_DIALOG);
        }
        //File doesn't exist.
        return false;
    }

    private void askUserToOpenFolder() {
        System.out.println(GlobalConfiguration.RESPONSE_RESULT_SUCCESS);
        boolean isYes = InputValidator.getYesOrNoAnswer("Do you want to open " + this.dir + " in your File Explorer?");
        if (isYes) {
            this.openFolder();
        }
    }

}
