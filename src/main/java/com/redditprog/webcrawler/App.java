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

        while (!isFinished) {
            //We're not 1.0 yet :D
            System.out.println("Reddit Photo Extractor 0.2");

            askUser(scanner);

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

    public static void askUser(Scanner scanner) {
        //Here we get the Operating Systems' name
        String os = System.getProperty("os.name");
        boolean isValid;
        String sub;

        while (true) {
            System.out.println("What subbredit do you want to download from?");
            sub = scanner.next();
            
            isValid = SubRedditChecker.verifySubReddit(sub);
            if (!isValid) {
                System.out.println("No such subreddit exist! try again.\n\n");
            } else {
                break;
            }
        }

        System.out.println("Enter how many pictures do you want to download: ");
        int num_pics = scanner.nextInt();

        System.out.println("Top links from which period: hour, day, week, month, year, all");
        String top_time = scanner.next();

        // If top_time is not set to any of the choices except "all", then
        // the value is set to "all" by default
        if (!(top_time.contains("hour") || top_time.contains("day") || top_time.contains("week")
                || top_time.contains("month") || top_time.contains("year"))) {
            top_time = "all";
        }

        String dir;
        System.out.println("Do you want to save in the current working directory? y(es)/n(o)");
        String answer = scanner.next().toLowerCase();

        if (answer.equals("y") || answer.equals("yes")) {
            dir = System.getProperty("user.dir");
            if (os.startsWith("Windows")) {
                dir += "\\";
            }
        } else if (answer.equals("n") || answer.equals("no")) {
            System.out.println("Enter the path you want to save the pictures in: ");
            dir = scanner.next();
            if (!dir.endsWith("\\") && os.startsWith("Windows")) {
                dir += "\\";
            }
        } else {
            dir = "";
        }

        Extractor anExtractor = new Extractor(num_pics, sub, dir, top_time);
        anExtractor.beginExtract();
    }
}
