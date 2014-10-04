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
        boolean isYes;
        while (!isFinished) {

            launcher = new Launcher(scanner);
            launcher.start();
            
            isYes = InputValidator.getYesOrNoAnswer(GlobalConfiguration.QUESTION_START_AGAIN);
            if(isYes) {
            	System.out.println("\n");
            	continue;
            }
            else {
                isFinished = true;
                System.out.println(GlobalConfiguration.EXIT_MESSAGE);
            }
        }
        scanner.close();
    }
}
