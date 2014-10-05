/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redditprog.webcrawler;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author ryan
 */
final class GlobalConfiguration {

    protected static final String WELCOME_MESSAGE = "Reddit Photo Extractor v0.1-alpha";
    protected static final String EXIT_MESSAGE = "Have a good day and thank you for using Reddit Photo Extractor!";

    protected static final int MAX_PICS_ALLOWED = 500;
    protected static final int TOTAL_ITEMS_PER_PAGE = 25;
    protected static final int TOTAL_SEARCH_LIMIT = 1000;

    // Windows default download path
    protected static final String WINDOWS_TARGET_PATH = "C:\\Users\\Public\\Pictures\\";

    // Questions for user
    protected static final String QUESTION_START_AGAIN = "Do you want to start again?";
    protected static final String QUESTION_SUB = "What subbredit do you want to download from?";
    protected static final String QUESTION_DIR = "Do you want to save in the default folder?";
    protected static final String QUESTION_DIR_SUBREDDIT = "Do you want to put the images in the subreddit's folder?";
    protected static final String QUESTION_USER_PREF_PATH = "Enter the path you want to save the pictures in: ";
    protected static final String QUESTION_TYPE_LINKS = "Images from which period: hot, new, rising, "
            + "controversial, top or gilded?";
    protected static final String QUESTION_TOP_TIME = "Top links from which period: hour, day, week, month, year, all";
    protected static final String QUESTION_NUM_PICS = "Enter how many pictures do you want to download: ";
    protected static final String QUESTION_ALBUM_DOWNLOAD = "Do you want to download it?";
    protected static final String FILE_ALREADY_EXISTS_DIALOG = "This file already exists! Would you like to overwrite it?";
    protected static final String QUESTION_GET_YES_NO = "(y)es/(n)o";

    // Invalid responses from user input
    protected static final String INVALID_RESPONSE_SUB = "No such subreddit exist! try again.\n\n";
    protected static final String INVALID_RESPONSE_YES_NO = "Invalid answer. Please answer (y)es or (n)o.";
    protected static final String INVALID_RESPONSE_TYPE_LINKS = "Invalid choice! Please try again: ";
    protected static final String INVALID_RESPONSE_NUM_PICS_INT = "That is not a valid number. Please try again.";
    protected static final String INVALID_RESPONSE_NUM_PICS_MAX = "You can't download more than 500 pictures. Enter again.";
    protected static final String INVALID_RESPONSE_INVALID_FOLDER = "Ooops, looks like this folder doesn't exist :(";
    protected static final String INVALID_RESPONSE_BLACKLISTED_SUB = "That subreddit does not contains any pictures. Please try again: ";

    // Result responses
    protected static final String RESPONSE_RESULT_FAIL = "There weren't enough pictures for your request.";
    protected static final String RESPONSE_RESULT_SUCCESS = "==================\n"
            + "Download finished!\n"
            + "==================";
    protected static final String NO_MORE_PICS_FOUND = "==================\nThere are no more pictures to be found! Extraction finished\n";

    protected static final String FILE_ALREADY_EXISTS_NOTIFICATION = "The file already exists! Download was skipped.";
    protected static final String INVALID_FOLDER = "This folder does not exist or you have got insufficient permissions! Please re-do the process: ";
    protected static final String INVALID_CLIENT_ID_IMGUR_AUTHORIZATION = "Warning! Cannot authorize imgur connection!";

    // OS
    protected static final String USER_OS = System.getProperty("os.name");
    protected static final String OS_WINDOWS = "Windows";
    protected static final String OS_LINUX = "Linux";

    // Links ranges
    protected static final List<String> LIST_TYPE_LINKS = Arrays.asList("hot", "new", "rising",
            "controversial", "top", "gilded");
    protected static final List<String> LIST_BLACKLISTED_SUB = Arrays.asList("videos");

    // URLs and patterns
    protected static final String REDDIT_PRE_SUB_URL = "http://reddit.com/r/";
    protected static final String IMGUR_API_ALBUM_URL = "https://api.imgur.com/3/album/";
    protected static final String IMGUR_API_IMAGE_URL = "https://api.imgur.com/3/image/";
    protected static final String IMGUR_ALBUM_URL_PATTERN = "imgur.com/a/";
    protected static final String IMGUR_SINGLE_URL_PATTERN = "i.imgur.com";
    protected static final String IMGUR_CHECK_STRING = "imgur";

}
