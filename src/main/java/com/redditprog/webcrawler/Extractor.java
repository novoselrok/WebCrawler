/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redditprog.webcrawler;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Scanner;

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
    private final String top_time;
    private final Scanner scanner;
    private final boolean isNsfw;
    private HtmlPage nsfwPage;

    /**
     * The class constructor with the following parameters:
     *
     * @param sub SubReddit name
     * @param num Number of pictures to extract
     * @param dir Directory path to save photos
     * @param top_time Range of Date/Time for the subreddit
     * @param scanner Scanner object pass from Launcher
     * @param statusSub Flag status to tell
     */
    public Extractor(String sub, int num, String dir,
            String top_time, Scanner scanner, boolean statusSub) {
        this.sub = sub;
        this.num_pics = num;
        this.dir = dir;
        this.top_time = top_time;
        this.scanner = scanner;
        this.isNsfw = statusSub;

    }

    public void beginExtract() {
        try {
            // set the full url of the user input subreddit
            final URL url = new URL("http://reddit.com/r/" + this.sub);

            // create a new connection
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();

            // connects to the http
            huc.connect();

            // Extract the redirect url in string
            InputStream is;
            String redirectURL;
            try {
                is = huc.getInputStream();
                redirectURL = huc.getURL().getPath();
                if (redirectURL.contains("/r/")) {
                    this.downloadPhotos();
                    return;
                }

                if (redirectURL.contains("over18")) {

                    WebClient aWebClient = new WebClient(BrowserVersion.CHROME);
                    aWebClient.getOptions().setJavaScriptEnabled(false);

                    System.out.println(huc.getURL());
                    // Get the first page
                    final HtmlPage page1 = aWebClient.getPage(huc.getURL());

                    // Get the form that we are dealing with and within that form, 
                    // find the submit button and the field that we want to change.
                    List<HtmlForm> listOfForms = page1.getForms();

                    System.out.println("Entering WebClient mode...");
                    for (HtmlForm a : listOfForms) {
                        if (a.getAttribute("action").isEmpty()) {
                            List<HtmlButton> listOfButtons = a.getButtonsByName("over18");
                            for (HtmlButton aButton : listOfButtons) {
                                if (aButton.getAttribute("value").contains("yes")) {
                                    final HtmlPage anHtmlPage = aButton.click();

                                    this.downloadPhotosByWebClient(anHtmlPage);
                                    // for debugging only (do not delete)
                                    //System.out.println(nextPage.getUrl());
                                    break;
                                }

                            }
                            break;
                        }

                    }
                    aWebClient.closeAllWindows();

                } else {
                }
                is.close();

            } catch (Exception e) {

            }
        } catch (Exception e) {
        }
    }

    private void downloadPhotosByWebClient(HtmlPage aHtmlPage) throws IOException {
        HtmlImage image;
        final List<?> images = aHtmlPage.getByXPath("//img");
        int numTitle = 0;
        int counter = 0;

        if (images.size() - 1 < this.num_pics) {
            System.out.println("far too many request");
        }

        for (Object imageObject : images) {
            if (numTitle == 0) {
                numTitle += 1;
                counter += 1;
                continue;
            }
            
            image = (HtmlImage) imageObject;
            
            // Create new arbritrary file to save photo later
            File file = new File(this.dir + Integer.toString(numTitle) + "."
                    + image.getImageReader().getFormatName().toLowerCase());
            
            while (file.exists()) {
                numTitle += 1;
                file = new File(this.dir + Integer.toString(numTitle) + "."
                    + image.getImageReader().getFormatName().toLowerCase());
            }
            
            
            image.saveAs(file);
            
            numTitle += 1;

            System.out.println("==================");
            System.out.println("Download #" + counter + " complete:");
            System.out.println("Name of the file: " + file.getPath());

            if (this.num_pics == counter) {
                break;
            }
            counter += 1;
        }
        
        this.askUserToOpenFolder();
    }

    private void downloadPhotos() {
        //System.out.println("Your pictures will be saved in: " + this.dir);
        System.out.println("Entering JSOUP area....");

        Document page;
        int i = 0;
        try {
            String url = "http://www.reddit.com/r/" + this.sub + "/top/?sort=top&t=" + this.top_time;
            System.out.println(url);
            while (i != this.num_pics) {

                page = Jsoup.connect(url).get();
                String next_page = page.select("a[rel=nofollow next]").attr("href");

                //Selecting all the elements with HTML class "title", 
                //that have nested inside <a href="..">..</a> tags
                //that end with jpg or png
                Elements images = page.select(".title").select("a[href$=jpg], a[href$=png]");

                if (images.isEmpty()) {
                    System.out.println("empty images");
                }
                for (Element link : images) {

                    if (i == this.num_pics) {
                        break;
                    }

                    //Saving the url of the picture
                    URL addr = new URL(link.attr("href"));
                    InputStream in = addr.openStream();
                    OutputStream op = null;
                    String[] tab = link.attr("href").split("/");

                    if (new File(this.dir + tab[tab.length - 1]).exists()) {
                        System.out.println("File [" + (this.dir + tab[tab.length - 1]) + "] already exists.\nAnother picture will be downloaded instead.");
                        continue;
                    }

                    try {
                        op = new FileOutputStream(this.dir + tab[tab.length - 1]);
                    } catch (FileNotFoundException e) {
                        System.out.println("You have entered an invalid path. Shutting down...");
                        System.exit(-1);
                    }

                    //Saving the picture to the file
                    savePicture(in, op);

                    in.close();
                    op.close();

                    System.out.println("==================");
                    System.out.println("Download #" + (i + 1) + " complete: " + link.text());
                    System.out.println("Name of the file: " + tab[tab.length - 1]);

                    i++;
                }
                //Here we substitute the original url, with the next page one
                url = next_page;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.askUserToOpenFolder();
    }

    private static void savePicture(InputStream in, OutputStream op) {
        byte[] b = new byte[20480];
        try {
            int length;
            while ((length = in.read(b)) != -1) {
                op.write(b, 0, length);
            }
        } catch (IOException e) {
            System.out.println("An error occured while saving the picture.");
        }
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
                openFolder();
                isSelected = true;
            } else if (openFolder.equalsIgnoreCase("n") || openFolder.equalsIgnoreCase("no")) {
                isSelected = true;
            } else {
                System.out.println("Enter y or n");
            }
        }
    }

}
