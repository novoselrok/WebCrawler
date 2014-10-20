/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redditprog.webcrawler;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author ryan
 */
public class LauncherTest {

    protected static final String OS = System.getProperty("os.name");
    protected static final String OS_WINDOWS = "Windows";
    protected static final String OS_LINUX = "Linux";

    protected static final String WINDOWS_DEFAULT_PATH = "C:\\Users\\Public\\Pictures\\";

    public LauncherTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of start method, of class Launcher.
     */
    @Ignore
    public void testStart() {
        System.out.print("\nMethod: ");
        System.out.println("start");
        Map<String, String> userHistory = null;
        
        
        Launcher instance = new Launcher(new Scanner(System.in));
        instance.start(userHistory);
        
        String expted_sub = "aww";
        String expted_dir = "";
        String expted_type_links = "hot";
        String expted_top_time = "hour";
        int expted_num_pics = 1;
        
        assertEquals("Expected subreddit does not match. ", 
                expted_sub, instance.getSubMember());
        assertEquals("Expected directory does not match. ", 
                expted_dir, instance.getDirMember());
        assertEquals("Expected type of link does not match. ", 
                expted_type_links, instance.getTypeLinksMember());
        assertEquals("Expected top time type does not match. ", 
                expted_top_time, instance.getTopTime());
        assertEquals("Expected number of pictures does not match. ", 
                expted_num_pics, instance.getNumPics());
    }

    /**
     * Test of getSub method, of class Launcher.
     */
    @Test (timeout = 7000)
    public void testGetSubValid() {
        System.out.print("\nMethod: ");
        System.out.println("getSub");

        String input_sub = "aww";
        System.setIn(new java.io.ByteArrayInputStream(input_sub.getBytes()));

        Launcher instance = new Launcher(new Scanner(System.in));

        String expResult = "aww";
        String result = instance.getSub();
        System.out.println(input_sub);

        assertEquals(expResult, result);
        assertTrue(!result.isEmpty());
    }
    
     /**
     * Test of getSub method, of class Launcher.
     */
    @Test (timeout = 7000)
    public void testGetSubInvalid() {
        System.out.print("\nMethod: ");
        System.out.println("getSub");

        String input_sub = "jahsdkjash\n"
                        +   "iwksoaskda\n"
                        +   "aww\n";

        Launcher instance = new Launcher(new Scanner(input_sub));

        String expResult = "aww";
        String result = instance.getSub();

        assertEquals(expResult, result);
        assertTrue(!result.isEmpty());
    }

    /**
     * Test of getDir method, of class Launcher for default path and no
     * subreddit folder.
     */
    @Test
    public void testGetDirDefaultPathNoSubFolder() {
        System.out.print("\nMethod: ");
        System.out.println("getDir");

        String sub = "aww";
        String expResult = "";
        String input_list = "y\n"
                + "n\n";

        Launcher instance = new Launcher(new Scanner(input_list));
        instance.setSub(sub);

        if (OS.startsWith(OS_LINUX)) {
            expResult = System.getProperty("user.dir") + "/";
        } else if (OS.startsWith(OS_WINDOWS)) {
            expResult = WINDOWS_DEFAULT_PATH;
        }

        String result = instance.getDir();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDir method, of class Launcher for default path and with
     * subreddit folder.
     */
    @Test
    public void testGetDirDefaultPathYesSubFolder() {
        System.out.print("\nMethod: ");
        System.out.println("getDir");

        String sub = "aww";
        String expResult = "";
        String input_list = "y\n"
                + "y\n";

        Launcher instance = new Launcher(new Scanner(input_list));
        instance.setSub(sub);

        if (OS.startsWith(OS_LINUX)) {
            expResult = System.getProperty("user.dir") + "/" + sub + "/";
        } else if (OS.startsWith(OS_WINDOWS)) {
            expResult = WINDOWS_DEFAULT_PATH + sub + "\\";
        }

        String result = instance.getDir();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDir method, of class Launcher for custom path and no subreddit
     * folder.
     */
    @Test
    public void testGetDirCustomPathNoSubFolder() {
        System.out.print("\nMethod: ");
        System.out.println("getDir");

        String sub = "aww";
        String expResult = "";
        String input_list = "";

        if (OS.startsWith(OS_LINUX)) {
            input_list = "n\n"
                    + "/home/ryan/Downloads\n"
                    + "n\n";
            expResult = "/home/ryan/Downloads/";
        } else if (OS.startsWith(OS_WINDOWS)) {
            input_list = "n\n"
                    + WINDOWS_DEFAULT_PATH + "\n"
                    + "n\n";
            expResult = WINDOWS_DEFAULT_PATH;
        }

        Launcher instance = new Launcher(new Scanner(input_list));
        instance.setSub(sub);

        String result = instance.getDir();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDir method, of class Launcher for custom path and with 
     * subreddit folder.
     */
    @Test
    public void testGetDirCustomPathYesSubFolder() {
        System.out.print("\nMethod: ");
        System.out.println("getDir");

        String sub = "aww";
        String expResult = "";
        String input_list = "";

        if (OS.startsWith(OS_LINUX)) {
            input_list = "n\n"
                    + "/home/ryan/Downloads\n"
                    + "y\n";
            expResult = "/home/ryan/Downloads/" + sub + "/";
        } else if (OS.startsWith(OS_WINDOWS)) {
            input_list = "n\n"
                    + WINDOWS_DEFAULT_PATH + "\n"
                    + "y\n";
            expResult = WINDOWS_DEFAULT_PATH + sub + "\\";
        }

        Launcher instance = new Launcher(new Scanner(input_list));
        instance.setSub(sub);

        String result = instance.getDir();
        assertEquals(expResult, result);
    }

    /**
     * Test of addSubredditFolder method, of class Launcher for "y" input
     */
    @Test
    public void testAddSubredditFolderInputY() {
        System.out.print("\nMethod: ");
        System.out.println("addSubredditFolder");

        String input_sub = "aww";

        String input_dir = "";
        if (OS.startsWith(OS_LINUX)) {
            input_dir = "/home/ryan/Downloads/";
        } else if (OS.startsWith(OS_WINDOWS)) {
            input_dir = WINDOWS_DEFAULT_PATH;
        }

        String input_yes = "y";
        System.setIn(new java.io.ByteArrayInputStream(input_yes.getBytes()));

        Launcher instance = new Launcher(new Scanner(System.in));

        instance.setSub(input_sub);
        String expResult = input_dir + input_sub + "/";

        String result = instance.addSubredditFolder(input_dir);
        System.out.println(input_yes);

        assertEquals(expResult, result);
    }

    /**
     * Test of addSubredditFolder method, of class Launcher for "yes" input
     */
    @Test
    public void testAddSubredditFolderInputYes() {
        System.out.print("\nMethod: ");
        System.out.println("addSubredditFolder");

        String input_sub = "aww";

        String input_dir = "";
        if (OS.startsWith(OS_LINUX)) {
            input_dir = "/home/ryan/Downloads/";
        } else if (OS.startsWith(OS_WINDOWS)) {
            input_dir = WINDOWS_DEFAULT_PATH;
        }

        String input_yes = "yes";
        System.setIn(new java.io.ByteArrayInputStream(input_yes.getBytes()));

        Launcher instance = new Launcher(new Scanner(System.in));

        instance.setSub(input_sub);
        String expResult = input_dir + input_sub + "/";

        String result = instance.addSubredditFolder(input_dir);
        System.out.println(input_yes);

        assertEquals(expResult, result);
    }

    /**
     * Test of addSubredditFolder method, of class Launcher for "n" input
     */
    @Test
    public void testAddSubredditFolderInputN() {
        System.out.print("\nMethod: ");
        System.out.println("addSubredditFolder");

        String input_sub = "aww";

        String input_dir = "";
        if (OS.startsWith(OS_LINUX)) {
            input_dir = "/home/ryan/Downloads/";
        } else if (OS.startsWith(OS_WINDOWS)) {
            input_dir = WINDOWS_DEFAULT_PATH;
        }

        String input_no = "n";
        System.setIn(new java.io.ByteArrayInputStream(input_no.getBytes()));

        Launcher instance = new Launcher(new Scanner(System.in));

        instance.setSub(input_sub);
        String expResult = input_dir;

        String result = instance.addSubredditFolder(input_dir);
        System.out.println(input_no);

        assertEquals(expResult, result);

    }

    /**
     * Test of addSubredditFolder method, of class Launcher for "no" input
     */
    @Test
    public void testAddSubredditFolderInputNo() {
        System.out.print("\nMethod: ");
        System.out.println("addSubredditFolder");

        String input_sub = "aww";

        String input_dir = "";
        if (OS.startsWith(OS_LINUX)) {
            input_dir = "/home/ryan/Downloads/";
        } else if (OS.startsWith(OS_WINDOWS)) {
            input_dir = WINDOWS_DEFAULT_PATH;
        }

        String input_no = "no";
        System.setIn(new java.io.ByteArrayInputStream(input_no.getBytes()));

        Launcher instance = new Launcher(new Scanner(System.in));

        instance.setSub(input_sub);
        String expResult = input_dir;

        String result = instance.addSubredditFolder(input_dir);
        System.out.println(input_no);

        assertEquals(expResult, result);
    }

    /**
     * Test of isValidfolder method, of class Launcher for valid folder.
     */
    @Test
    public void testIsValidfolderValid() {
        System.out.print("\nMethod: ");
        System.out.println("isValidfolder");
        String directory = "";

        if (OS.startsWith(OS_LINUX)) {
            directory = "/home/ryan/Downloads";
        } else if (OS.startsWith(OS_WINDOWS)) {
            directory = WINDOWS_DEFAULT_PATH;
        }

        Launcher instance = new Launcher(new Scanner(System.in));

        boolean expResult = true;
        boolean result = instance.isValidfolder(directory);
        assertEquals(expResult, result);
    }

    /**
     * Test of isValidfolder method, of class Launcher for invalid folder.
     */
    @Test
    public void testIsValidfolderInvalid() {
        System.out.print("\nMethod: ");
        System.out.println("isValidfolder");
        String directory = "";

        if (OS.startsWith(OS_LINUX)) {
            directory = "/Documents/";
        } else if (OS.startsWith(OS_WINDOWS)) {
            directory = "C:\\Home\\";
        }

        Launcher instance = new Launcher(new Scanner(System.in));

        boolean expResult = false;
        boolean result = instance.isValidfolder(directory);
        assertEquals(expResult, result);
    }

    /**
     * Test of getTypeOfLinks method, of class Launcher.
     */
    @Test
    public void testGetTypeOfLinks() {
        System.out.print("\nMethod: ");
        System.out.println("getTypeOfLinks");

        String input_type = "hot";
        System.setIn(new java.io.ByteArrayInputStream(input_type.getBytes()));

        Launcher instance = new Launcher(new Scanner(System.in));

        String expResult = "hot";
        String result = instance.getTypeOfLinks();
        System.out.println(input_type);

        assertEquals(expResult, result);
    }
    
    /**
     * Test of getTypeOfLinks method, of class Launcher for multiple invalid
     * inputs then a valid input
     */
    @Test
    public void testGetTypeOfLinksMultipleInvalid() {
        System.out.print("\nMethod: ");
        System.out.println("getTypeOfLinks");

        String input_type = "\n"
                            + " \n"
                            + "hey\n"
                            + "hot";

        Launcher instance = new Launcher(new Scanner(input_type));

        String expResult = "hot";
        String result = instance.getTypeOfLinks();

        assertEquals(expResult, result);
    }

    /**
     * Test of getTopTime method, of class Launcher for valid input.
     */
    @Test
    public void testGetTopTimeValid() {
        System.out.print("\nMethod: ");
        System.out.println("getTopTime");

        String input_top_time = "hour";
        System.setIn(new java.io.ByteArrayInputStream(input_top_time.getBytes()));

        Launcher instance = new Launcher(new Scanner(System.in));

        String expResult = "hour";
        String result = instance.getTopTime();
        System.out.println(input_top_time);

        assertEquals(expResult, result);
    }

    /**
     * Test of getTopTime method, of class Launcher for empty input.
     */
    @Test (expected = NoSuchElementException.class)
    public void testGetTopTimeEmpty() {
        System.out.print("\nMethod: ");
        System.out.println("getTopTime");

        String input_top_time = "\n"
                               + " \n";
        
        Launcher instance = new Launcher(new Scanner(input_top_time));

        String result = instance.getTopTime();
    }

    /**
     * Test of getTopTime method, of class Launcher for invalid input.
     */
    @Test
    public void testGetTopTimeInvalid() {
        System.out.print("\nMethod: ");
        System.out.println("getTopTime");

        String input_top_time = "hello";
        System.setIn(new java.io.ByteArrayInputStream(input_top_time.getBytes()));

        Launcher instance = new Launcher(new Scanner(System.in));

        String expResult = "all";
        String result = instance.getTopTime();
        System.out.println(input_top_time);

        assertEquals(expResult, result);
    }

    /**
     * Test of getNumPics method, of class Launcher.
     */
    @Test
    public void testGetNumPics() {
        System.out.print("\nMethod: ");
        System.out.println("getNumPics");

        String input_num_pics = "2";
        System.setIn(new java.io.ByteArrayInputStream(input_num_pics.getBytes()));

        Launcher instance = new Launcher(new Scanner(System.in));

        int expResult = 2;
        int result = instance.getNumPics();
        System.out.println(input_num_pics);

        assertEquals(expResult, result);
    }
    
    /**
     * Test of getNumPics method, of class Launcher for multiple invalid inputs
     * and then a valid input.
     */
    @Test
    public void testGetNumPicsMultipleInvalid() {
        System.out.print("\nMethod: ");
        System.out.println("getNumPics");

        String input_num_pics = "asl\n"
                            +   "1283\n"
                            +   "4\n";

        Launcher instance = new Launcher(new Scanner(input_num_pics));

        int expResult = 4;
        int result = instance.getNumPics();
        System.out.println(input_num_pics);

        assertEquals(expResult, result);
    }

}
