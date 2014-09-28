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
        Launcher launcher;
        while (!isFinished) {
            //askUser(scanner);
            launcher = new Launcher(scanner);
            launcher.start();

            String reply;
            while (true) {
                System.out.print("Do you want to start again?(y/n): ");
                reply = scanner.next();

                if ((reply.equalsIgnoreCase("y")) || (reply.equalsIgnoreCase("n"))) {
                    break;
                } else {
                    System.out.println("That is not a valid input. Please try again (y/n):");
                }
            }
            System.out.println("\n");

            if (reply.equalsIgnoreCase("n")) {
                isFinished = true;
                System.out.println("Have a good day and thank you for using Reddit Photo Extractor!");
            }
        }
        scanner.close();
    }
}
