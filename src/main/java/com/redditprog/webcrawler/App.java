package com.redditprog.webcrawler;

import java.util.Scanner;

/**
 * A small-ish webcrawler
 *
 * @author Rok
 * @author Ryan
 */
public class App {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean isFinished = false;
        //We're not 1.0 yet :D
        System.out.println("Reddit Photo Extractor v0.1-alpha");
        Launcher launcher = null;
        while (!isFinished) {
            //askUser(scanner);
        	launcher = new Launcher();
        	launcher.start();
        	
            System.out.print("Do you want to extract more photos?(y/n): ");
            String reply = scanner.next();
            System.out.println("\n");

            if (reply.equalsIgnoreCase("n")) {
                isFinished = true;
                System.out.println("Have a good day and thank you for using Reddit Photo Extractor!");
            }
        }
        scanner.close();
    }    
}
