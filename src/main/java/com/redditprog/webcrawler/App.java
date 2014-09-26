package com.redditprog.webcrawler;

import java.util.Scanner;

/**
 * A small-ish webcrawler
 * @author Rok
 * @author Ryan
 */
public class App {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        //Here we get the Operating Systems' name
        String os = System.getProperty("os.name");
        
        System.out.println("What subbredit do you want to download from?");
        String sub = scanner.next();
        
        System.out.println("Enter how many pictures do you want to download: ");
        int num_pics = scanner.nextInt();

        System.out.println("Top links from which period: hour, day, week, month, year, all");
        String top_time = scanner.next();
        
        String dir;
        System.out.println("Do you want to save in the current working directory? y(es)/n(o)");
        String answer = scanner.next().toLowerCase();
        
        if(answer.equals("y") || answer.equals("yes")){
        	dir = System.getProperty("user.dir");
        	if(os.startsWith("Windows")) dir += "\\";
        }else if(answer.equals("n") || answer.equals("no")){
        	System.out.println("Enter the path you want to save the pictures in: ");
        	dir = scanner.next();
        	if(!dir.endsWith("\\") && os.startsWith("Windows")) dir += "\\";
        }else{
        	dir = "";
        }
        
        Extractor anExtractor = new Extractor(num_pics, sub, dir, top_time);
        anExtractor.beginExtract();
        
        scanner.close();
    }
}
