/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redditprog.webcrawler;

import java.awt.Desktop;
import java.io.File;
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
    
    // Constructor
    public Extractor(Launcher launcher) {
    	this.sub = launcher.getSub();
    	this.num_pics = launcher.getNumPics();
    	this.dir = launcher.getDir();
    	this.top_time = launcher.getTopTime();
    }

    public void beginExtract() {
    	//System.out.println("Your pictures will be saved in: " + this.dir);
        Document page;
        int i = 0;
        try {
        	String url = "http://www.reddit.com/r/"+ this.sub + "/top/?sort=top&t=" + this.top_time;
        	while(i != this.num_pics){
        		
	            page = Jsoup.connect(url).get();
	            String next_page = page.select("a[rel=nofollow next]").attr("href");
	            
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
	                
	                if(new File(this.dir + tab[tab.length - 1]).exists()) {
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
        System.out.println("==================");
        System.out.println("Download finished!");
        System.out.println("==================");
        System.out.println("Do you want to open " + this.dir + "\nin your File Explorer? (y/n)");
        boolean isSelected = false;
        Scanner s;
        while(!isSelected){
        	s = new Scanner(System.in);
        	String openFolder = s.next();
        	if(openFolder.equalsIgnoreCase("y") || openFolder.equalsIgnoreCase("yes")){
        		openFolder();
        		isSelected = true;
        	}else if(openFolder.equalsIgnoreCase("n") || openFolder.equalsIgnoreCase("no")){
        		isSelected = true;
        	}else{
        		System.out.println("Enter y or n");
        	}
        }
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
    
    private void openFolder(){
    	try {
			Desktop.getDesktop().open(new File(this.dir));
		} catch (IOException e) {
			System.out.println("Ooops, looks like this folder doesn't exist :(");
			e.printStackTrace();
		}
    }


}
