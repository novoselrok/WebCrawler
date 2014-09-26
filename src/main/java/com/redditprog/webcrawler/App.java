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
        
        System.out.println("Enter how many pictures do you want to download: ");
        int num_pics = scanner.nextInt();

        System.out.println("What subbredit do you want to download from?");
        String sub = scanner.next();

        System.out.println("What directory do you want to save in? Ex: C:\\Users\\<your_username>\\Desktop\\");
        String dir = scanner.next();

        Extractor anExtractor = new Extractor(num_pics, sub, dir);
        anExtractor.beginExtract();
        
        scanner.close();
    }
}
