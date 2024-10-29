/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package traveldart;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import javax.swing.JOptionPane;
import java.io.*;


/**
 *
 * @author aj216
 */
public class HomePageTest {
    private static HomePage homePage;
    
    public HomePageTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        homePage = new HomePage(){
            protected int showConfirmDialog(String message) {
                return JOptionPane.YES_OPTION; // Simulate "Yes" response
            }    
        };
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
    
    @Test
    public void testSignOffMouseClicked_ConfirmsAndSignsOff() {
        int response = JOptionPane.showConfirmDialog(null, "Are you sure?");
        if (response == JOptionPane.YES_OPTION) {
            // Logic to go to the login screen
            System.out.println("User signed off. Redirecting to login...");
        } else {
            System.out.println("Sign off cancelled.");
        }
        
    }
    
    public void testSignOffMouseClicked_Cancelled() {
       homePage = new HomePage() {
            //@Override
            protected int showConfirmDialog(String message) {
                return JOptionPane.NO_OPTION; // Simulate "No" response
            }
        };

        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        homePage.performSignOff(); // Call the method

        // Verify output
        assertTrue(outputStreamCaptor.toString().contains("Sign Off cancelled"));
    }

    /**
     * Test of main method, of class HomePage.
     */
    @Test
    public void testMain() {
        String[] args = null;
        HomePage.main(args);
        
    }
    
}
