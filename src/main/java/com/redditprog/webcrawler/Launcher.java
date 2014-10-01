package com.redditprog.webcrawler;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Launcher {
	private final static int MAX_PICS = 500; 
	private final String OS = System.getProperty("os.name");
	private final Scanner scanner;
	private String sub;
	private String dir;
	private int num_pics;
	private String type_of_links;
	private String top_time;

	public Launcher(Scanner scanner) {
		this.scanner = scanner;
	}

	public void start() {
		this.sub = this.getSub();
		this.num_pics = this.getNumPics();
		this.type_of_links = this.getTypeOfLinks();
		
		if(type_of_links.equals("top")){
			this.top_time = getTopTime();
		}else{
			this.top_time = "";
		}
		
		this.dir = this.getDir();
		Extractor extractor = new Extractor(this.sub, this.num_pics, this.dir,
				this.type_of_links,this.top_time, this.scanner);
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
				System.out
						.println("Invalid answer. Please answer (y)es or (n)o.");
			}
		}
		return dir_temp;
	}

	private String getTypeOfLinks() {
		// Immutable ArrayList of options
		List<String> listOfOptions = Arrays.asList("hot", "new", "rising",
				"controversial", "top", "gilded");
		boolean isAcceptable = false;

		// Ask user for range of links for a subreddit
		System.out.println("Images from which period: hot, new, rising, "
				+ "controversial, top or gilded?");
		String type_of_links_temp = scanner.next();

		// Force user to enter valid input
		while (!isAcceptable) {
			for (String choice : listOfOptions) {
				if (type_of_links_temp.equals(choice)) {
					isAcceptable = true;
				}
			}

			if (!isAcceptable) {
				System.out.println("Invalid choice! Please try again: ");
				type_of_links_temp = scanner.next();
			}
		}
		return type_of_links_temp;
	}

	private String getTopTime() {
		// Ask user for range of links for a subreddit
		System.out.println("Top links from which period: hour, day, week, month, year, all");
		String top_time = scanner.next();

		// If top_time is not set to any of the choices except "all", then
		// the value is set to "all" by default
		if (!(top_time.contains("hour") || top_time.contains("day")
			|| top_time.contains("week") || top_time.contains("month") || top_time.contains("year"))) {
			top_time = "all";
		}

		return top_time;
	}

	private int getNumPics() {
		System.out.println("Enter how many pictures do you want to download: ");

		while (!scanner.hasNextInt()) {
			System.out.println("That is not a valid number. Please try again.");
			scanner.next();
		}
		int num_pics_temp = 0;
		
		while(true){
			num_pics_temp = scanner.nextInt();
			if(num_pics_temp < MAX_PICS) break;
			else System.out.println("You can't download more than 500 pictures. Enter again.");
		}
		return num_pics_temp;
	}
}
