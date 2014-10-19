package com.redditprog.webcrawler;

import java.util.Scanner;

public class InputValidator {

    public static boolean getYesOrNoAnswer(String question, Scanner scanner) {
        boolean isSelected = false;
        boolean isYes = false;
        String answer = "";
        while (!isSelected) {
            System.out.println(question + " " + GlobalConfiguration.QUESTION_GET_YES_NO);
            answer = scanner.next();

            if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) {
                isSelected = true;
                isYes = true;
            } else if (answer.equalsIgnoreCase("n") || answer.equalsIgnoreCase("no")) {
                isSelected = true;
            } else {
                System.out.println(GlobalConfiguration.INVALID_RESPONSE_YES_NO);
            }
        }
        return isYes;
    }
}
