/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redditprog.webcrawler;

import java.util.Map;
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
        System.out.println("start");
        Map<String, String> userHistory = null;
        Launcher instance = null;
        instance.start(userHistory);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDirectory method, of class Launcher.
     */
    @Ignore
    public void testGetDirectory() {
        System.out.println("getDirectory");
        Launcher instance = null;
        String expResult = "";
        String result = instance.getDirectory();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSubReddit method, of class Launcher.
     */
    @Ignore
    public void testGetSubReddit() {
        System.out.println("getSubReddit");

        Launcher instance = null;
        String expResult = "";
        String result = instance.getSubReddit();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSub method, of class Launcher.
     */
    @Test
    public void testGetSubValid() {
        System.out.println("getSub");
        
        String input_sub = "aww";
        System.setIn(new java.io.ByteArrayInputStream(input_sub.getBytes()));
        
        Launcher instance = new Launcher(new Scanner(System.in));
        String expResult = "aww";
        String result = instance.getSub();
        
        assertEquals(expResult, result);
        assertTrue(!result.isEmpty());
    }

    /**
     * Test of getDir method, of class Launcher.
     */
    @Ignore
    public void testGetDir() {
        System.out.println("getDir");
        Launcher instance = null;
        String expResult = "";
        String result = instance.getDir();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addSubredditFolder method, of class Launcher.
     */
    @Test
    public void testAddSubredditFolder() {
        System.out.println("addSubredditFolder");
        
        String input_sub = "aww";
        String input_dir = "/home/ryan/Downloads/";
        String input_yes = "y";
        System.setIn(new java.io.ByteArrayInputStream(input_yes.getBytes()));
        
        
        Launcher instance = new Launcher(new Scanner(System.in));
        instance.setSub(input_sub);
        String expResult = input_dir + input_sub + "/";
        
        String result = instance.addSubredditFolder(input_dir);
        
        assertEquals(expResult, result);
    }

    /**
     * Test of isValidfolder method, of class Launcher.
     */
    @Ignore
    public void testIsValidfolder() {
        System.out.println("isValidfolder");
        String directory = "";
        Launcher instance = null;
        boolean expResult = false;
        boolean result = instance.isValidfolder(directory);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTypeOfLinks method, of class Launcher.
     */
    @Ignore
    public void testGetTypeOfLinks() {
        System.out.println("getTypeOfLinks");
        Launcher instance = null;
        String expResult = "";
        String result = instance.getTypeOfLinks();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTopTime method, of class Launcher.
     */
    @Ignore
    public void testGetTopTime() {
        System.out.println("getTopTime");
        Launcher instance = null;
        String expResult = "";
        String result = instance.getTopTime();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNumPics method, of class Launcher.
     */
    @Ignore
    public void testGetNumPics() {
        System.out.println("getNumPics");
        Launcher instance = null;
        int expResult = 0;
        int result = instance.getNumPics();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
