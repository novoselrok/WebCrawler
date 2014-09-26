/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redditprog.webcrawler;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
    
    // Constructor
    public Extractor(int num_pics, String sub, String dir, String top_time) {
        this.num_pics = num_pics;
        this.sub = sub;
        this.dir = dir;
        this.top_time = top_time;
    }

    public void beginExtract() {
        Document page;
        int i = 0;
        try {
        	
            page = Jsoup.connect("http://www.reddit.com/r/"+ this.sub + "/top/?sort=top&t=" + this.top_time).get();

            //Selecting all the elements with HTML class "title", 
            //that have nested inside <a href="..">..</a> tags
            //that end with jpg or png
            Elements images = page.select(".title").select("a[href$=jpg], a[href$=png]");
            
            for (Element link : images) {
            	
                if (i == this.num_pics) {
                    break;
                }

                //Saving the url of the picture
                URL addr = new URL(link.attr("href"));
                InputStream in = addr.openStream();
                OutputStream op = null;
                String[] tab = link.attr("href").split("/");
                
                try {
                    if ((link.attr("href").endsWith("jpg")) || (link.attr("href").endsWith("png"))) {
                        op = new FileOutputStream(this.dir + tab[tab.length - 1]);
                    } else {
                        System.out.println("Sorry, no dice.. yet");
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("You have entered an invalid path. Try again.");
                    System.exit(-1);
                }
                //Saving the picture to the file
                savePicture(in, op);
                
                in.close();
                op.close();
                
                System.out.println("==================");
                System.out.println("Download complete: " + link.attr("href"));
                System.out.println("File has been saved in: " + this.dir + tab[tab.length - 1]);
                System.out.println("==================");
                
                i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("==================");
        System.out.println("Download finished!");
        
    }
    
    private static void savePicture(InputStream in, OutputStream op){
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


}
