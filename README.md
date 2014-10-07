[![Build Status](https://travis-ci.org/novoselrok/WebCrawler.svg?branch=master)](https://travis-ci.org/novoselrok/WebCrawler)

WebCrawler
==========

A simple webcrawler, that downloads pictures from a subreddit of your choice.

<br/>
Features
--------

- Allows to download png, jpg or jpeg files up to 500 max hosted from imgur
- Subreddit oriented
- Filter pictures to download by hot, new, rising, controversial, top and gilded
- Filter top pictures by this hour, today, this week, this month, this year and all


<br/>
Dependencies:
------------

- HtmlUnit
- Json

<br/>
Installation
----------

For Maven, add dependencies to your pom.xml:
```xml
    <dependency>
      <groupId>net.sourceforge.htmlunit</groupId>
      <artifactId>htmlunit</artifactId>
      <version>2.15</version>
    </dependency>
    <dependency>
      <groupId>org.json</groupId>
      <artifactId>json</artifactId>
      <version>20090211</version>
    </dependency>
```


<br/>
Rough TODO List
-----

- Finalize v1.0 release
- Sort out any known bugs
- ~~Provide a Linux path example at 3. question~~
- ~~Check if the picture is already downloaded~~
- ~~Check for invalid paths and make the user re-enter them~~
- ~~Add support for more than 25 pictures~~


<br/>
Roadmap
-------

- Build on top of GUI of some sort
- Better preformance
- More filters
- Add search functionality
- Support for static gif files

<br/>
Collaboration
------------
Feel free to open an issue, if you want to fix any bugs or throw a suggestion.

<br/>
ChangeLog
---------

###v0.1-alpha
- Download the top pictures of any (existing) subreddit
- It supports as many pictures as you want
- Select top links from this day, week, month, year or all time best


<br/>
License
-------
MIT license