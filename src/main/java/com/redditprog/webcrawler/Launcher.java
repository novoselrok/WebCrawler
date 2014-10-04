package com.redditprog.webcrawler;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;

public class Launcher {

    private final String OS = GlobalConfiguration.USER_OS;
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

        if (type_of_links.equals("top")) {
            this.top_time = getTopTime();
        } else {
            this.top_time = "";
        }

        this.dir = this.getDir();
        Extractor extractor = new Extractor(this.sub, this.num_pics, this.dir,
                this.type_of_links, this.top_time);
        extractor.beginExtract();
    }

    private String getSub() {
        boolean isValid = false;
        boolean isBlackListed = false;
        String sub_temp = "";

        // Ask user for subreddit name and verifies it
        while (!isValid) {
            System.out.println(GlobalConfiguration.QUESTION_SUB);
            sub_temp = scanner.next();

            isValid = SubRedditChecker.verifySubReddit(sub_temp);

            if (!isValid) {
                System.out.println(GlobalConfiguration.INVALID_RESPONSE_SUB);
            } else {

                for (String subReddit : GlobalConfiguration.LIST_BLACKLISTED_SUB) {
                    if (sub_temp.equalsIgnoreCase(subReddit)) {
                        isBlackListed = true;
                    }
                }

                if (isBlackListed) {
                    System.out.println(GlobalConfiguration.INVALID_RESPONSE_BLACKLISTED_SUB);
                    isBlackListed = false;
                    isValid = false;
                }

            }
        }

        return sub_temp;
    }

    private String getDir() {
        String dir_temp = "";
        
        boolean isYes;
        isYes = InputValidator.getYesOrNoAnswer(GlobalConfiguration.QUESTION_DIR);
        if (isYes) {
            if (OS.startsWith(GlobalConfiguration.OS_WINDOWS)) {
                dir_temp = GlobalConfiguration.WINDOWS_TARGET_PATH;
            } else if (OS.startsWith(GlobalConfiguration.OS_LINUX)) {
                dir_temp = System.getProperty("user.dir") + "/";
            }
        } else {
            System.out.println(GlobalConfiguration.QUESTION_USER_PREF_PATH);
            dir_temp = scanner.next();
            if (!dir_temp.endsWith("\\") && OS.startsWith(GlobalConfiguration.OS_WINDOWS)) {
                dir_temp += "\\";
            } else if (!dir_temp.endsWith("/") && OS.startsWith(GlobalConfiguration.OS_LINUX)) {
                dir_temp += "/";
            }
            if(!isValidfolder(dir_temp)){
            	System.out.println(GlobalConfiguration.INVALID_FOLDER);
            	return getDir();
            }
        }
        
        dir_temp = addSubredditFolder(dir_temp);
        return dir_temp;
    }
    
    private String addSubredditFolder(String dir_temp){
        boolean isYes = InputValidator.getYesOrNoAnswer(GlobalConfiguration.QUESTION_DIR_SUBREDDIT);
        if(isYes){
        	//Create the folder.
            if (OS.startsWith(GlobalConfiguration.OS_WINDOWS)) {
                dir_temp += sub + "\\";
            } else if (OS.startsWith(GlobalConfiguration.OS_LINUX)) {
                dir_temp += sub + "/";
            }
            //Creating the folder if it doesn't exist
        	File file = new File(dir_temp);
        	file.mkdir();
        }
        return dir_temp;
    }
    
    private boolean isValidfolder(String directory){
    	File file = new File(directory);
    	return file.isDirectory() && file.canWrite() && file.canRead() && Files.isReadable(file.toPath()) && Files.isWritable(file.toPath());
    }

    private String getTypeOfLinks() {
        // Immutable ArrayList of options
        List<String> listOfOptions = GlobalConfiguration.LIST_TYPE_LINKS;

        boolean isAcceptable = false;

        // Ask user for range of links for a subreddit
        System.out.println(GlobalConfiguration.QUESTION_TYPE_LINKS);
        String type_of_links_temp = scanner.next();

        // Force user to enter valid input
        while (!isAcceptable) {
            for (String choice : listOfOptions) {
                if (type_of_links_temp.equals(choice)) {
                    isAcceptable = true;
                }
            }

            if (!isAcceptable) {
                System.out.println(GlobalConfiguration.INVALID_RESPONSE_TYPE_LINKS);
                type_of_links_temp = scanner.next();
            }
        }
        return type_of_links_temp;
    }

    private String getTopTime() {
        // Ask user for range of links for a subreddit
        System.out.println(GlobalConfiguration.QUESTION_TOP_TIME);
        String top_time_temp = scanner.next();

        // If top_time is not set to any of the choices except "all", then
        // the value is set to "all" by default
        if (!(top_time_temp.contains("hour") || top_time_temp.contains("day")
                || top_time_temp.contains("week") || top_time_temp.contains("month") || top_time_temp.contains("year"))) {
            top_time_temp = "all";
        }

        return top_time_temp;
    }

    private int getNumPics() {
        System.out.println(GlobalConfiguration.QUESTION_NUM_PICS);

        while (!scanner.hasNextInt()) {
            System.out.println(GlobalConfiguration.INVALID_RESPONSE_NUM_PICS_INT);
            scanner.next();
        }
        int num_pics_temp = 0;

        while (true) {
            num_pics_temp = scanner.nextInt();
            if (num_pics_temp < GlobalConfiguration.MAX_PICS_ALLOWED) {
                break;
            } else {
                System.out.println(GlobalConfiguration.INVALID_RESPONSE_NUM_PICS_MAX);
            }
        }
        return num_pics_temp;
    }
}
