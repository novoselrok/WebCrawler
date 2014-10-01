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

        System.out.println(GlobalConfiguration.WELCOME_MESSAGE);
        Launcher launcher;
        while (!isFinished) {

            launcher = new Launcher(scanner);
            launcher.start();

            String reply;
            while (true) {
                System.out.print(GlobalConfiguration.QUESTION_START_AGAIN);
                reply = scanner.next();

                if ((reply.equalsIgnoreCase("y")) || (reply.equalsIgnoreCase("n"))) {
                    break;
                } else {
                    System.out.println(GlobalConfiguration.INVALID_RESPONSE_START_AGAIN);
                }
            }
            System.out.println("\n");

            if (reply.equalsIgnoreCase("n")) {
                isFinished = true;
                System.out.println(GlobalConfiguration.EXIT_MESSAGE);
            }
        }
        scanner.close();
    }
}
