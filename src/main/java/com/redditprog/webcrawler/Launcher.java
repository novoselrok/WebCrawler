package com.redditprog.webcrawler;

import java.util.Scanner;

public class Launcher {
	
	private final String OS = System.getProperty("os.name");
	private Scanner scanner;
	private Extractor extractor;

	public void start() {
		scanner = new Scanner(System.in);
		extractor = new Extractor(this);
		extractor.beginExtract();
	}

	public String getSub() {
        boolean isValid = false;
        String sub = "";

        // Ask user for subreddit name and verifies it
        while (!isValid) {
            System.out.println("What subbredit do you want to download from?");
            sub = scanner.next();

            isValid = SubRedditChecker.verifySubReddit(sub);
            if (!isValid) {
                System.out.println("No such subreddit exist! try again.\n\n");
            }
        }
        return sub;
	}

	public String getDir() {
		String dir = "";
        boolean isSelectedDir = false;
        while (!isSelectedDir) {
            System.out.println("Do you want to save in the default folder? y(es)/n(o)");
            String answer = scanner.next().toLowerCase();

            if (answer.equals("y") || answer.equals("yes")) {                
                if (OS.startsWith("Windows")) {
                    dir = "C:\\Users\\Public\\Pictures\\";
                }else if (OS.startsWith("Linux")) {
                    dir = System.getProperty("user.dir") + "/";                    
                }      
                
                isSelectedDir = true;
            } else if (answer.equals("n") || answer.equals("no")) {
                System.out.println("Enter the path you want to save the pictures in: ");
                dir = scanner.next();
                if (!dir.endsWith("\\") && OS.startsWith("Windows")) {
                    dir += "\\";
                }else if(!dir.endsWith("/") && OS.startsWith("Linux")){
                	dir += "/";
                }
                
            	isSelectedDir = true;                
            } else {
                System.out.println("Invalid answer. Please answer y(es) or n(o).");
            }
        }
        return dir;
	}

	public String getTopTime() {
		// Ask user for range of links for a  subreddit
        System.out.println("Top links from which period: hour, day, week, month, year, all");
        String top_time = scanner.next();

        // If top_time is not set to any of the choices except "all", then
        // the value is set to "all" by default
        if (!(top_time.contains("hour") || top_time.contains("day") || top_time.contains("week")
                || top_time.contains("month") || top_time.contains("year"))) {
            top_time = "all";
        }

        return top_time;
	}

	public int getNumPics() {
		System.out.println("Enter how many pictures do you want to download: ");

        while (!scanner.hasNextInt()) {
            System.out.println("That is not a valid number. Please try again.");
            scanner.next();
        }

        int num_pics = scanner.nextInt();        
        return num_pics;
	}
}
