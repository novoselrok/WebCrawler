package com.redditprog.webcrawler;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Launcher {

    private final String OS = GlobalConfiguration.USER_OS;
    private final Scanner scanner;
    private String sub;
    private String dir;
    private int numPics;
    private String typeOfLinks;
    private String topTime;

    public Launcher(Scanner scanner) {
        this.scanner = scanner;
    }

    public void start(Map<String, String> userHistory) {
        this.sub = this.getSub();
        this.numPics = this.getNumPics();
        this.typeOfLinks = this.getTypeOfLinks();

        if (typeOfLinks.equals("top")) {
            this.topTime = getTopTime();
        } else {
            this.topTime = "";
        }

        if (userHistory.keySet().contains(this.sub)) {
            this.dir = userHistory.get(this.sub);
        } else {
            this.dir = this.getDir();
        }

        Extractor extractor = new Extractor(this.sub, this.numPics, this.dir,
                this.typeOfLinks, this.topTime);
        extractor.beginExtract();
    }

    public String getDirectory() {
        return this.dir;
    }

    public String getSubReddit() {
        return this.sub;
    }

    private String getSub() {
        int statusCode = 0;
        boolean isBlackListed = false;
        String subTemp = "";

        // Ask user for subreddit name and verifies it
        while (statusCode != 1) {
            System.out.println(GlobalConfiguration.QUESTION_SUB);
            subTemp = scanner.next().toLowerCase();

            statusCode = SubRedditChecker.verifySubReddit(subTemp);

            switch (statusCode) {
                case 0:
                    System.out.println(GlobalConfiguration.INVALID_RESPONSE_SUB);
                    break;
                case 1:
                    for (String subReddit : GlobalConfiguration.LIST_BLACKLISTED_SUB) {
                        if (subTemp.equalsIgnoreCase(subReddit)) {
                            isBlackListed = true;
                        }
                    }

                    if (isBlackListed) {
                        System.out.println(GlobalConfiguration.INVALID_RESPONSE_BLACKLISTED_SUB);
                        isBlackListed = false;
                        statusCode = 0;
                    }
                    break;
                case 2:
                    System.out.println(GlobalConfiguration.RESPONSE_BUSY);
                    break;
                default:
                    break;
            }
        }

        return subTemp;
    }

    private String getDir() {
        String dirTemp = "";

        boolean isYes;
        isYes = InputValidator.getYesOrNoAnswer(GlobalConfiguration.QUESTION_DIR);
        if (isYes) {
            if (OS.startsWith(GlobalConfiguration.OS_WINDOWS)) {
            	dirTemp = GlobalConfiguration.WINDOWS_TARGET_PATH;
            } else if (OS.startsWith(GlobalConfiguration.OS_LINUX)) {
            	dirTemp = System.getProperty("user.dir") + "/";
            }
        } else {
            System.out.println(GlobalConfiguration.QUESTION_USER_PREF_PATH);
            dirTemp = scanner.next();
            if (!dirTemp.endsWith("\\") && OS.startsWith(GlobalConfiguration.OS_WINDOWS)) {
            	dirTemp += "\\";
            } else if (!dirTemp.endsWith("/") && OS.startsWith(GlobalConfiguration.OS_LINUX)) {
            	dirTemp += "/";
            }
            if (!isValidfolder(dirTemp)) {
                System.out.println(GlobalConfiguration.INVALID_FOLDER);
                return getDir();
            }
        }

        dirTemp = addSubredditFolder(dirTemp);
        return dirTemp;
    }

    private String addSubredditFolder(String dir) {
        boolean isYes = InputValidator.getYesOrNoAnswer(GlobalConfiguration.QUESTION_DIR_SUBREDDIT);
        if (isYes) {
            //Create the folder.
            if (OS.startsWith(GlobalConfiguration.OS_WINDOWS)) {
                dir += sub + "\\";
            } else if (OS.startsWith(GlobalConfiguration.OS_LINUX)) {
            	dir += sub + "/";
            }
            //Creating the folder if it doesn't exist
            File file = new File(dir);
            file.mkdir();
        }
        return dir;
    }

    private boolean isValidfolder(String directory) {
        File file = new File(directory);
        return file.isDirectory() && file.canWrite() && file.canRead() && Files.isReadable(file.toPath()) && Files.isWritable(file.toPath());
    }

    private String getTypeOfLinks() {
        // Immutable ArrayList of options
        List<String> listOfOptions = GlobalConfiguration.LIST_TYPE_LINKS;

        boolean isAcceptable = false;

        // Ask user for range of links for a subreddit
        System.out.println(GlobalConfiguration.QUESTION_TYPE_LINKS);
        String typeOfLinksTemp = scanner.next();

        // Force user to enter valid input
        while (!isAcceptable) {
            for (String choice : listOfOptions) {
                if (typeOfLinksTemp.equals(choice)) {
                    isAcceptable = true;
                }
            }

            if (!isAcceptable) {
                System.out.println(GlobalConfiguration.INVALID_RESPONSE_TYPE_LINKS);
                typeOfLinksTemp = scanner.next();
            }
        }
        return typeOfLinksTemp;
    }

    private String getTopTime() {
        // Ask user for range of links for a subreddit
        System.out.println(GlobalConfiguration.QUESTION_TOP_TIME);
        String topTimeTemp = scanner.next();

        // If top_time is not set to any of the choices except "all", then
        // the value is set to "all" by default
        if (!(topTimeTemp.contains("hour") || topTimeTemp.contains("day")
                || topTimeTemp.contains("week") || topTimeTemp.contains("month") || topTimeTemp.contains("year"))) {
        	topTimeTemp = "all";
        }

        return topTimeTemp;
    }

    private int getNumPics() {
        System.out.println(GlobalConfiguration.QUESTION_NUM_PICS);

        while (!scanner.hasNextInt()) {
            System.out.println(GlobalConfiguration.INVALID_RESPONSE_NUM_PICS_INT);
            scanner.next();
        }
        int numPicsTemp = 0;

        while (true) {
        	numPicsTemp = scanner.nextInt();
            if (numPicsTemp < GlobalConfiguration.MAX_PICS_ALLOWED) {
                break;
            } else {
                System.out.println(GlobalConfiguration.INVALID_RESPONSE_NUM_PICS_MAX);
            }
        }
        return numPicsTemp;
    }
}
