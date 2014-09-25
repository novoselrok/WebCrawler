package com.redditprog.webcrawler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * A small-ish webcrawler
 * 
 */
public class App {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter how many pictures do you want to download: ");
		int num_pics = scanner.nextInt();

		System.out.println("What subbredit do you want to download from?");
		String sub = scanner.next();
		
		System.out.println("What directory do you want to save in? Ex: C:\\Users\\<your_username>\\Desktop\\");
		String dir = scanner.next();
		
		Document page;
		int i = 0;
		try {
			page = Jsoup.connect("http://www.reddit.com/r/" + sub+ "/top/?sort=top&t=month").get();
			
			Elements images = page.select(".title").select("a[href$=jpg], a[href$=png]");

			for (Element link : images) {
				if (i == num_pics)
					break;
				// System.out.println(i + " " + link.attr("href"));
				URL addr = new URL(link.attr("href"));
				InputStream in = addr.openStream();
				OutputStream op;
				//TODO: If the specified path doesn't exist try and create it
				if (link.attr("href").endsWith("jpg")) {
					op = new FileOutputStream(dir + i + ".jpg");
				} else {
					op = null;
					System.out.println("Sorry, no dice.. yet");
				}
				byte[] b = new byte[20480];
				int length;
				while ((length = in.read(b)) != -1) {
					// writing it to a file
					op.write(b, 0, length);
				}
				in.close();
				op.close();
				i++;
				System.out.println("Download complete: " + link.attr("href"));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
