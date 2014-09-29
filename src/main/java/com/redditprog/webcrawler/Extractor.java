
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
    private final String range;
    private final Scanner scanner;

    /**
     * The class constructor with the following parameters:
     *
     * @param sub SubReddit name
     * @param num Number of pictures to extract
     * @param dir Directory path to save photos
     * @param top_time Range of Date/Time for the subreddit
     * @param scanner Scanner object pass from Launcher
     */
    public Extractor(String sub, int num, String dir,
            String top_time, Scanner scanner) {
        this.sub = sub;
        this.num_pics = num;
        this.dir = dir;
        this.range = top_time;
        this.scanner = scanner;

    }

    public void beginExtract() {

        // set the full url of the user input subreddit
        final URL urlSub, urlJson;

        try {
            urlSub = new URL("http://reddit.com/r/" + this.sub);
            urlJson = new URL("http://www.reddit.com/r/" + this.sub + "/"
                    + this.range + ".json");
        } catch (MalformedURLException ex) {
            Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        String jsonString = this.extractJsonFromUrl(urlJson);

        JSONObject obj;
        JSONArray childArray;
        int numDownloads = 0;

        try {
            obj = new JSONObject(jsonString);

            childArray = obj.getJSONObject("data").getJSONArray("children");

            mainloop:
            for (int i = 0; i < childArray.length(); i++) {
                String urlString = "";
                
                if (childArray.getJSONObject(i)
                        .getJSONObject("data").has("url")) {
                    urlString = childArray.getJSONObject(i)
                        .getJSONObject("data").getString("url");
                } else  {
                    // Gilded range change the Data's url child to link_url
                    if (childArray.getJSONObject(i)
                        .getJSONObject("data").has("link_url")) {
                        urlString = childArray.getJSONObject(i)
                        .getJSONObject("data").getString("link_url");
                    }
                }
                
                
                if (urlString.contains("imgur")) {
                    URL url = new URL(urlString);

                    if (urlString.substring(urlString.lastIndexOf("/"))
                            .contains(".")) {
                        numDownloads = this.extractImgurSingle(numDownloads, url);

                    } else {
                        numDownloads = this.extractImgurAlbum(numDownloads, url);
                    }

                    if (numDownloads == this.num_pics) {
                        break;
                    }

                }

            }
        } catch (JSONException ex) {
            Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
            return;
        } catch (MalformedURLException ex) {
            Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        if (numDownloads > 0) {
            if (numDownloads < this.num_pics) {
                System.out.println("=================================");
                System.out.println("There are no more pictures found.");
                System.out.println("=================================");
            }
            this.askUserToOpenFolder();
        } else {
            System.out.println("==================");
            System.out.println("No Pictures found!");
            System.out.println("==================");
        }

    }

    private int extractImgurSingle(int numDownloads, URL url) {

        String fileName = url.getFile();
        // + 1 because this.dir already got "/" as the last character
        String destName = this.dir + fileName.substring(fileName.lastIndexOf("/")+1);

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
            Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
            return numDownloads;
        }

        
    }

    private int extractImgurAlbum(int numDownloads, URL url) {
        HtmlPage imgurPage;
        HtmlImage image;

        // create a new connection
        HttpURLConnection huc;
        try {
            huc = (HttpURLConnection) url.openConnection();
            // connects to the http
            huc.connect();
            WebClient aWebClient = new WebClient(BrowserVersion.CHROME);
            aWebClient.getOptions().setJavaScriptEnabled(false);
            imgurPage = aWebClient.getPage(url);

            final List<?> images = imgurPage.getByXPath("//img");

            int numTitle = 1;
            String imageSrc;

            for (Object imageObject : images) {
                // Typecast to HtmlImage
                image = (HtmlImage) imageObject;

                imageSrc = image.getAttribute("src");

                // If the object found is not jpeg, jpg or png filetype, 
                // move to next object
                if (!imageSrc.contains(".")) {
                    continue;
                }
                if (!((imageSrc.contains(".jpeg"))
                        || (imageSrc.contains(".jpg"))
                        || (imageSrc.contains(".png")))) {
                    continue;
                }

                // Bypass any non-user submitted photo (e.g. website layout image)
                if (image.getHeight() < 200){
                    continue;
                }

                // Create new arbritrary file to save photo later
                File file;
                try {
                    file = new File(this.dir
                            + imageSrc.substring(imageSrc.lastIndexOf("/")+1));

                    while (file.exists()) {
                        numTitle += 1;
                        file = new File(this.dir
                            + numTitle + imageSrc.substring(imageSrc.lastIndexOf(".")));
                    }

                    image.saveAs(file);

                    numTitle += 1;

                    this.printDownloadCompleted(numDownloads, file.getPath());
                   

                    if (this.num_pics == numDownloads) {
                        break;
                    }
                    numDownloads += 1;

                    if (numDownloads == this.num_pics) {
                        break;
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
                    System.exit(1);
                }
                numTitle += 1;
            }

            aWebClient.closeAllWindows();
        } catch (IOException ex) {
            Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }

        return numDownloads;
    }
    
    private void filterPhoto() {
        
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

            return buffer.toString();

        } catch (IOException ex) {
            Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return "";
    }

    private void openFolder() {
        try {
            Desktop.getDesktop().open(new File(this.dir));
        } catch (IOException e) {
            System.out.println("Ooops, looks like this folder doesn't exist :(");
            e.printStackTrace();
        }
    }

    private void askUserToOpenFolder() {
        System.out.println("==================");
        System.out.println("Download finished!");
        System.out.println("==================");
        System.out.println("Do you want to open " + this.dir + "\nin your File Explorer? (y/n)");
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
