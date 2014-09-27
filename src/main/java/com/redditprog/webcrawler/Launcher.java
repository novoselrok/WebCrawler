package com.redditprog.webcrawler;

import java.util.Scanner;

public class Launcher {

    private final String OS = System.getProperty("os.name");
    private final Scanner scanner;
    private String sub;
    private String dir;
    private int num_pics;
    private String top_time;

    public Launcher(Scanner scanner) {
        this.scanner = scanner;
    }

    public void start() {
        this.sub = this.getSub();
        this.num_pics = this.getNumPics();
        this.top_time = this.getTopTime();
        this.dir = this.getDir();

        Extractor extractor = new Extractor(this.sub, this.num_pics,
                this.dir, this.top_time, this.scanner);
        extractor.beginExtract();
    }

    private String getSub() {
        boolean isValid = false;
        String sub_temp = "";

        // Ask user for subreddit name and verifies it
        while (!isValid) {
            System.out.println("What subbredit do you want to download from?");
            sub_temp = scanner.next();

            int statusSubReddit = SubRedditChecker.verifySubReddit(sub_temp);
            
            switch (statusSubReddit) {
                case 0:
                    isValid = false;
                    break;
                case 1:
                    isValid = true;
                    break;
                case 2: 
                    isValid = true;
                    break;
                default:
                    isValid = false;
                    break;
            }
            
            if (!isValid) {
                System.out.println("No such subreddit exist! try again.\n\n");
            }
        }
        return sub_temp;
    }

    private String getDir() {
        String dir_temp = "";
        boolean isSelectedDir = false;
        while (!isSelectedDir) {
            System.out.println("Do you want to save in the default folder? y(es)/n(o)");
            String answer = scanner.next().toLowerCase();

            if (answer.equals("y") || answer.equals("yes")) {
                if (OS.startsWith("Windows")) {
                    dir_temp = "C:\\Users\\Public\\Pictures\\";
                } else if (OS.startsWith("Linux")) {
                    dir_temp = System.getProperty("user.dir") + "/";
                }

                isSelectedDir = true;
            } else if (answer.equals("n") || answer.equals("no")) {
                System.out.println("Enter the path you want to save the pictures in: ");
                dir_temp = scanner.next();
                if (!dir_temp.endsWith("\\") && OS.startsWith("Windows")) {
                    dir_temp += "\\";
                } else if (!dir_temp.endsWith("/") && OS.startsWith("Linux")) {
                    dir_temp += "/";
                }

                isSelectedDir = true;
            } else {
                System.out.println("Invalid answer. Please answer y(es) or n(o).");
            }
        }
        return dir_temp;
    }

    private String getTopTime() {
        // Ask user for range of links for a  subreddit
        System.out.println("Top links from which period: hour, day, week, month, year, all");
        String top_time_temp = scanner.next();

        // If top_time is not set to any of the choices except "all", then
        // the value is set to "all" by default
        if (!(top_time_temp.contains("hour") || top_time_temp.contains("day") || top_time_temp.contains("week")
                || top_time_temp.contains("month") || top_time_temp.contains("year"))) {
            top_time_temp = "all";
        }

        return top_time_temp;
    }

    private int getNumPics() {
        System.out.println("Enter how many pictures do you want to download: ");

        while (!scanner.hasNextInt()) {
            System.out.println("That is not a valid number. Please try again.");
            scanner.next();
        }

        int num_pics_temp = scanner.nextInt();
        return num_pics_temp;
    }
}
