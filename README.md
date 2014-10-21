[![Build Status](https://travis-ci.org/novoselrok/WebCrawler.svg?branch=master)](https://travis-ci.org/novoselrok/WebCrawler)  [![Coverage Status](https://coveralls.io/repos/novoselrok/WebCrawler/badge.png?branch=master)](https://coveralls.io/r/novoselrok/WebCrawler?branch=master)
##Reddit Image Downloader
It's a simple reddit image downloader. You can select any subreddit as long as it exists and it's not empty. Any of these pages can be selected to download the pictures from: hot, new, rising, controversial, top or gilded. You can currently download up to 500 pictures and save them in the default directory (on Windows that is "C:\Users\Public\Pictures\" and on Linux it's the current working directory). Otherwise you can input a valid path yourself. The last option is whether you would like to save in the specific subreddit folder (e.g. pics/pic1.jpg, gameofthrones/pic33.jpg).

##Dependencies
- org.json
- JUnit

##Try it out (latest release: v1.0)
[Download the JAR](https://github.com/novoselrok/WebCrawler/releases/download/v1.0/WebCrawler-v1.0.jar)
```
cd /path/to/jar
java -jar WebCrawler-v0.1-alpha-jar-with-dependencies
```

##TODO list
- An "Express mode" -> one line input
- Fast mode for albums
- Fix imgur deleted links cause the program to launch an exception
- Refactor the code to follow Google's Java Coding guidelines
- Build a working GUI on top of it (Swing or JavaFX)

##Collaboration
- Feel free to open an issue, if you want to fix any bugs or throw a suggestion.

##Change log (v0.1-alpha)
- Download the top pictures of any (existing) subreddit
- It supports as many pictures as you want
- Select top links from this day, week, month, year or all time best

