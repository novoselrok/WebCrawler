package com.redditprog.webcrawler;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * @author Rok
 * @author ryan
 */
public class SubRedditChecker {

    public static int verifySubReddit(String sub) {
        int isVerified = 0;
        try {
            // set the full url of the user input subreddit
            final URL url = new URL("http://reddit.com/r/" + sub);

            // create a new connection
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();

            // connects to the http
            huc.connect();

            // Extract the redirect url in string
            InputStream is = null;
            String redirectURL = "";
            try {
                is = huc.getInputStream();
                redirectURL = huc.getURL().getPath();
                if (redirectURL.contains("/r/")) {
                    isVerified = 1;
                }

                if (redirectURL.contains("over18")) {
                    isVerified = 2;
                    WebClient aWebClient = new WebClient(BrowserVersion.CHROME);
                    aWebClient.getOptions().setJavaScriptEnabled(false);
                    
                    // Get the first page
                    final HtmlPage page1 = aWebClient.getPage(huc.getURL());
                    
                    // Get the form that we are dealing with and within that form, 
                    // find the submit button and the field that we want to change.
                    List<HtmlForm> listOfForms = page1.getForms();
                    
                    for (HtmlForm a: listOfForms) {
                        System.out.println(a);
                        if (a.getActionAttribute().isEmpty()) {
                            List<HtmlButton> listOfButtons = a.getButtonsByName("over18");
                            for(HtmlButton aButton:listOfButtons) {
                                if (aButton.getAttribute("value").contains("yes")) {
                                   final HtmlPage nextPage = aButton.click();
                                   
                                   
                                   // for debugging only (do not delete)
                                   //System.out.println(nextPage.getUrl());
                                }
                            }
                                    
                            
                        }
                    }
                    
                    //final HtmlPage page2 = button.click();

                    
                } else {
                    isVerified = 0;
                }

                is.close();
            } catch (IOException e) {
                System.out.println(e);
                //e.printStackTrace();

            }
            // checks if it is a redirect and return boolean value
            return isVerified;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
