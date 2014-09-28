package com.redditprog.webcrawler;


import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Launcher {

    private final String OS = System.getProperty("os.name");
    private final Scanner scanner;
    private String sub;
    private String dir;
    private int num_pics;
    private String range;

    public Launcher(Scanner scanner) {
        this.scanner = scanner;
    }

    public void start() {
        this.sub = this.getSub();
        this.num_pics = this.getNumPics();
        this.range = this.getTopTime();
        this.dir = this.getDir();
        
        Extractor extractor = new Extractor(this.sub, this.num_pics,
                this.dir, this.range, this.scanner);
        extractor.beginExtract();
    }

    private String getSub() {
        boolean isValid = false;
        String sub_temp = "";

        // Ask user for subreddit name and verifies it
        while (!isValid) {
            System.out.println("What subbredit do you want to download from?");
            sub_temp = scanner.next();

            isValid = SubRedditChecker.verifySubReddit(sub_temp);
            
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
            System.out.println("Do you want to save in the default folder? (y)es/(n)o");
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
                System.out.println("Invalid answer. Please answer (y)es or (n)o.");
            }
        }
        return dir_temp;
    }

    private String getTopTime() {
         // Immutable ArrayList of options
        List<String> listOfOptions = Arrays.asList("hot", 
                "new", "rising", "controversial", "top", "gilded", "promoted");
        boolean isAcceptable = false;
        
        // Ask user for range of links for a  subreddit
        System.out.println("Images from which period: hot, new, rising, "
                + "controversial, top, gilded or promoted?");
        String top_time_temp = scanner.next();
       
        // Force user to enter valid input
        while (!isAcceptable) {
            for (String choice: listOfOptions){
                if (top_time_temp.equals(choice)) {
                    isAcceptable = true;
                }
            }
            
            if (!isAcceptable) {
                System.out.println("Invalid choice! Please try again: ");
                top_time_temp = scanner.next();
            }
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
