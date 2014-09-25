package com.redditprog.webcrawler;


import java.util.Scanner;

/**
 * A small-ish webcrawler
 *
 */
public class App {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Extractor anExtractor = new Extractor(scanner);
        anExtractor.beginExtract();
    }
}
