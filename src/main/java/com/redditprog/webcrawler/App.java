package com.redditprog.webcrawler;

import java.util.HashMap;
import java.util.Map;
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

        // String 1 = subreddit, string 2 = saveddirectory
        Map<String, String> userHistory = new HashMap<String, String>();
        
        while (!isFinished) {

            launcher = new Launcher(scanner);
            launcher.start(userHistory);

            // if new round of information is not in history, add it to history
            if (!userHistory.keySet().contains(launcher.getSubReddit())) {
                userHistory.put(launcher.getSubReddit(), launcher.getDirectory());
            }

            isYes = InputValidator.getYesOrNoAnswer(GlobalConfiguration.QUESTION_START_AGAIN);

            if (isYes) {
                System.out.println("\n");
            } else {
                isFinished = true;
                System.out.println(GlobalConfiguration.EXIT_MESSAGE);
            }
        }
        scanner.close();
    }
}
