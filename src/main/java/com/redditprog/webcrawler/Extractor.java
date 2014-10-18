package com.redditprog.webcrawler;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Ryan
 * @author Rok
 */
public class Extractor {

	// Instance variables
	private final int numPics;
	private final String sub;
	private final String dir;
	private final String typeOfLinks;
	private final String topTime;

	/**
	 * The class constructor with the following parameters:
	 *
	 * @param sub SubReddit name
	 * @param num Number of pictures to extract
	 * @param dir Directory path to save photos
	 * @param typeOfLinks Reddit's links categories
	 * @param topTime Range of Date/Time for the subreddit
	 */
	public Extractor(String sub, int numPics, String dir, String typeOfLinks,
			String topTime) {
		this.sub = sub;
		this.numPics = numPics;
		this.dir = dir;
		this.typeOfLinks = typeOfLinks;
		this.topTime = topTime;
	}

	public void beginExtract() {
		// set the full url of the user input subreddit
		URL urlJSON;
		String subredditJSONURL;
		int numDownloads = 0;
		ArrayList<String> gildedLinks = new ArrayList<String>();
		try {
			if (typeOfLinks.equals("top")) {
				subredditJSONURL = GlobalConfiguration.REDDIT_PRE_SUB_URL + this.sub
						+ "/" + this.typeOfLinks + "/.json?sort=top&t="
						+ this.topTime;
			} else {
				subredditJSONURL = GlobalConfiguration.REDDIT_PRE_SUB_URL + this.sub
						+ "/" + this.typeOfLinks + "/.json";
			}
			urlJSON = new URL(subredditJSONURL);
		} catch (MalformedURLException ex) {
			Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null,
					ex);
			return;
		}

		JSONObject subredditJSONObject;
		JSONArray childArray;
		int count = GlobalConfiguration.TOTAL_ITEMS_PER_PAGE;
		String baseURL = subredditJSONURL;

		while (true) {
			String jsonString = this.extractJsonFromUrl(urlJSON);

			if (jsonString == null) {
				// If extraction fails, skip extraction
				// they do fail sometimes resulting an empty string due to exception
				System.out.println(GlobalConfiguration.RESPONSE_RESULT_FAIL);
				break;
			}else if (!jsonString.startsWith(GlobalConfiguration.REDDIT_JSON_PATTERN)) {
				// When traffic is high or any other reason,
				// the extracted string won't be in json file format
				System.out.println(GlobalConfiguration.RESPONSE_BUSY);
				System.exit(0);
			}

			try {
				subredditJSONObject = new JSONObject(jsonString);
				String after = subredditJSONObject.getJSONObject("data").getString("after");

				childArray = subredditJSONObject.getJSONObject("data").getJSONArray("children");

				for (int i = 0; i < childArray.length(); i++) {
					String urlString = this.getImageURL(childArray, i);
					URL url = new URL(urlString);

					// Skips duplicate child url links due to comments for gilded
					if (this.typeOfLinks.equals("gilded")) {
						if (gildedLinks.contains(urlString)) {
							continue;
						} else {
							gildedLinks.add(urlString);
						}
					} else if (urlString.contains("gallery")) {
						continue;
					}

					if (urlString.contains("imgur")) {
						if (urlString.contains(GlobalConfiguration.IMGUR_ALBUM_URL_PATTERN)) {
							numDownloads = this.extractImgurAlbum(numDownloads, url);
						} else if (urlString.contains(GlobalConfiguration.IMGUR_SINGLE_URL_PATTERN)) {
							numDownloads = this.extractSingle(numDownloads, url, "single");
						} else if (!isProperImageExtension(urlString)) {
							String id = urlString.substring(urlString.lastIndexOf("/") + 1);
							if (id.contains(",")) {
								String[] arrayOfIds = id.split(",");
								for (String arrayOfId : arrayOfIds) {
									numDownloads = extractPicFromImgurAPI(arrayOfId, numDownloads);
								}
							} else {
								numDownloads = extractPicFromImgurAPI(id, numDownloads);
							}
						}
					} else if (isProperImageExtension(urlString)) {
						numDownloads = this.extractSingle(numDownloads, url, "single");
					} else {
						continue;
					}

					if (numDownloads >= this.numPics) {
						break;
					}
				}

				// Filter submissions of less than 25 items
				if (after.equalsIgnoreCase("null")) {
					System.out.println(GlobalConfiguration.RESPONSE_RESULT_FAIL);
					break;
				}

				// Updates url for next page
				if (typeOfLinks.equals("top")) {
					subredditJSONURL = baseURL + "&count=" + count + "&after=" + after;
				} else {
					subredditJSONURL = baseURL + "?count=" + count + "&after=" + after;
				}
				urlJSON = new URL(subredditJSONURL);

				// Updates count
				count += GlobalConfiguration.TOTAL_ITEMS_PER_PAGE;

				// Limit searches up to 1000th submission
				if (count >= GlobalConfiguration.TOTAL_SEARCH_LIMIT && numDownloads < this.numPics) {
					System.out.println(GlobalConfiguration.RESPONSE_RESULT_FAIL);
					break;
				}
			} catch (JSONException ex) {
				Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE,
						null, ex);
			} catch (MalformedURLException ex) {
				Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE,
						null, ex);
			}

			if (numDownloads >= this.numPics) {
				this.askUserToOpenFolder();
				break;
			}
		}
	}

	private int extractPicFromImgurAPI(String id, int numDownloads) {
		try {
			String extractedJSON = extractImgurAPIJson(id, "image");
			if(extractedJSON == null){
				System.out.println("Something went wrong. :(");
				return numDownloads;
			}
			// This might due to http error code connection
			// Error code results extractedJson to be empty
			// Skips extraction

			JSONObject object = new JSONObject(extractedJSON);
			String imageLink = object.getJSONObject("data").getString("link");
			if (!this.isLinkValid(imageLink)) {
				System.out.println(GlobalConfiguration.RESPONSE_ITEM_DELETED);
				return numDownloads;
			}

			URL imageURL = new URL(imageLink);
			numDownloads = this.extractSingle(numDownloads, imageURL, "single");
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return numDownloads;
	}

	// The last final validation
	// This is necessary because IMGUR data.link can result to
	// "http://..../.jpg"
	// with no filename at all, due to recent picture deletion (few minutes)
	// Recent picture delection in imgur, does not guarantee it is not
	// connectable
	// It can bypass http connection test, so this is a necessary check
	private boolean isLinkValid(String aLink) {
		String file = aLink.substring(aLink.lastIndexOf("/") + 1);
		return !file.startsWith(".");
	}

	private boolean isProperImageExtension(String image) {
		return image.endsWith("jpg") || image.endsWith("png")
				|| image.endsWith("jpeg") || image.endsWith("gif");
	}

	private String getImageURL(JSONArray childArray, int iteration) {
		String urlString = null;
		try {
			if (childArray.getJSONObject(iteration).getJSONObject("data").has("url")) {
				urlString = childArray.getJSONObject(iteration).getJSONObject("data").getString("url");
			} else if (childArray.getJSONObject(iteration).getJSONObject("data").has("link_url")) {
				// Gilded range change the Data's url child to link_url
				urlString = childArray.getJSONObject(iteration).getJSONObject("data").getString("link_url");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return urlString;
	}

	private int extractSingle(int numDownloads, URL url, String newMap) {
		String fileName = url.getFile();
		// + 1 because this.dir already got "/" as the last character
		String destName;
		if (newMap.equals("single")) {
			destName = this.dir
					+ fileName.substring(fileName.lastIndexOf("/") + 1);
		} else {
			destName = this.dir + newMap + File.separator
					+ fileName.substring(fileName.lastIndexOf("/") + 1);
		}

		if (destName.contains("?")) {
			destName = destName.substring(0, destName.length() - 2);
		}

		if (imageIsDuplicate(destName, url)) {
			return numDownloads;
		}

		// Verify http connection of the link
		boolean isVerifiedLink = this.verifyHttpConnection(url);

		if (!isVerifiedLink) {
			return numDownloads;
		}

		InputStream is;
		OutputStream os;
		try {
			is = url.openStream();
			os = new FileOutputStream(destName);
			byte[] b = new byte[2048];
			int length;

			while ((length = is.read(b)) != -1) {
				os.write(b, 0, length);
			}

			is.close();
			os.close();

			this.printDownloadCompleted(numDownloads, destName);
		} catch (FileNotFoundException ex) {
			Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null,
					ex);
			// Invalid file save needs to terminate the application,
			// saving the next image will result to the same error
			// Value of 1 (argument) or any > 0 number means building error has
			// occured
			System.exit(1);
		} catch (IOException exc) {
			Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null,
					exc);
			return numDownloads;
		}
		return numDownloads + 1;
	}

	private int extractImgurAlbum(int numDownloads, URL url) {
		String[] urlSplit = url.toString().split("/");
		String imgurAlbumID = urlSplit[urlSplit.length - 1];
		JSONObject imgurAlbumJSONObject;
		JSONArray imagesArray;
		String extractedJson = extractImgurAPIJson(imgurAlbumID, "album");

		// This might due to http error code connection
		// Skips album extraction
		if (extractedJson == null) {
			System.out.println("Album extraction skipped. :(");
			return numDownloads;
		}

		try {
			imgurAlbumJSONObject = new JSONObject(extractedJson);

			imagesArray = imgurAlbumJSONObject.getJSONObject("data").getJSONArray("images");
			String albumTitle = imgurAlbumJSONObject.getJSONObject("data").getString("title");
			int albumNumPics = imgurAlbumJSONObject.getJSONObject("data").getInt("images_count");
			System.out.println("==================");
			System.out.println("An album detected! Title is: " + albumTitle
					+ " Number of pics: " + albumNumPics);
			if (this.isAlbumDuplicate(imgurAlbumID)) {
				return numDownloads;
			}

			new File(this.dir + imgurAlbumID + File.separator).mkdir();

			for (int i = 0; i < imagesArray.length(); i++) {
				numDownloads = extractSingle(numDownloads, 
						new URL(imagesArray.getJSONObject(i).getString("link")), imgurAlbumID);
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return numDownloads;
	}

	private boolean isAlbumDuplicate(String albumName) {
		File rootFolder = new File(this.dir);
		String[] items = rootFolder.list();

		for (String item : items) {
			if (new File(this.dir + item).isDirectory() && item.equalsIgnoreCase(albumName)) {
				System.out.println(GlobalConfiguration.ALBUM_ALREADY_EXISTS_NOTIFICATION);
				return true;
			}
		}

		return false;
	}

	private void printDownloadCompleted(int num, String path) {
		System.out.println("==================");
		System.out.println("Download #" + (num + 1) + " complete:");
		System.out.println("Name of the file: " + path);
	}

	// Authorization is done twice, once for checking http connection through
	// HEAD,
	// the other authorisation for json extraction though GET
	// Because authorisation is needed for every api service
	private String extractImgurAPIJson(String id, String apiType) {
		StringBuilder jsonString = new StringBuilder();
		URL jsonUrl;
		try {
			if (apiType.equals("album")) {
				jsonUrl = new URL(GlobalConfiguration.IMGUR_API_ALBUM_URL + id);
			} else if (apiType.equals("image")) {
				jsonUrl = new URL(GlobalConfiguration.IMGUR_API_IMAGE_URL + id);
			} else {
				return null;
			}

			// Creates new HttpUTLConnection via jsonURL
			HttpURLConnection.setFollowRedirects(false);
			HttpURLConnection conn = (HttpURLConnection) jsonUrl.openConnection();

			// Authorize connection first
			this.authorizeImgurConnection(conn);

			// Verify http connection of link
			// Some pictures are private in imgur
			boolean isVerifiedLink = this.verifyHttpConnection(jsonUrl);

			if (!isVerifiedLink) {
				return null;
			}

			BufferedReader bin = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line;
			while ((line = bin.readLine()) != null) {
				jsonString.append(line);
			}

			bin.close();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		return jsonString.toString();
	}

	private boolean verifyHttpConnection(URL jsonUrl) {
		HttpURLConnection aConnection;
		try {
			aConnection = (HttpURLConnection) jsonUrl.openConnection();

			this.authorizeImgurConnection(aConnection);

			aConnection.setRequestMethod("HEAD");
			int responseCode = aConnection.getResponseCode();
			String msg = aConnection.getResponseMessage();

			if (responseCode != HttpURLConnection.HTTP_OK) {
				if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
					System.out.println(GlobalConfiguration.RESPONSE_ITEM_PRIVATE);
				} else {
					System.out.println(GlobalConfiguration.RESPONSE_ITEM_UNREACHABLE);
				}

				System.out.println("HTTP error code: " + responseCode + " " + msg);
				return false;
			} else {
				return true;
			}
		} catch (IOException ex) {
			Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null,
					ex);
			return false;
		}
	}

	private void authorizeImgurConnection(HttpURLConnection connection) {
		boolean isAuthorised = false;
		try {
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Authorization", "Client-ID "
					+ ClientIDClass.CLIENT_ID);

			isAuthorised = true;

		} catch (ProtocolException ex) {
			Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null,
					ex);
		}

		// Exits application if client id for imgur api is invalid
		if (!isAuthorised) {
			System.out.println(GlobalConfiguration.INVALID_CLIENT_ID_IMGUR_AUTHORIZATION);
			System.exit(1);
		}
	}

	private String extractJsonFromUrl(URL url) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuilder buffer = new StringBuilder();
			int read;
			char[] chars = new char[1024];
			while ((read = reader.read(chars)) != -1) {
				buffer.append(chars, 0, read);
			}
			reader.close();
			return buffer.toString();

		} catch (IOException ex) {
			Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null,
					ex);
		}
		return null;
	}

	private void openFolder() {
		try {
			Desktop.getDesktop().open(new File(this.dir));
		} catch (IOException e) {
			System.out
					.println(GlobalConfiguration.INVALID_RESPONSE_INVALID_FOLDER);
			e.printStackTrace();
		}
	}

	/**
	 * Function that makes sure to avoid downloading identical files. If a
	 * conflict is found asks the user to manually solve it.
	 *
	 * @param destNamethe file destination path
	 * @param url image source
	 * @return if the image download has to be skipped (true)
	 */
	private boolean imageIsDuplicate(String destName, URL url) {
		File file = new File(destName);
		boolean isImgurLink = url.toString().contains(GlobalConfiguration.IMGUR_CHECK_STRING);
		// The file exists and it's being downloaded from imgur so its ID is
		// unique -> It's a duplicate.
		if (file.exists() && isImgurLink) {
			System.out.println("==================\n" + url + " ---> "
					+ GlobalConfiguration.FILE_ALREADY_EXISTS_NOTIFICATION);
			return true;
		} else if (file.exists() && !isImgurLink) {
			// Asking user if he wants to overwrite.
			return !InputValidator.getYesOrNoAnswer("==================\n"
					+ url + " --> "
					+ GlobalConfiguration.FILE_ALREADY_EXISTS_DIALOG);
		}
		// File doesn't exist.
		return false;
	}

	private void askUserToOpenFolder() {
		System.out.println(GlobalConfiguration.RESPONSE_RESULT_SUCCESS);
		boolean isYes = InputValidator.getYesOrNoAnswer("Do you want to open "
				+ this.dir + " in your File Explorer?");
		if (isYes) {
			this.openFolder();
		}
	}

}
