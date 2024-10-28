/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package traveldart;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 *
 * @author aj216
 */
public class LoginTesterTest {
    
    
    public LoginTesterTest() {
    }
    
     @Test
    public void testIsValidPassword_ValidPassword() {
        assertTrue(SignUp.isValidPassword("Password1!"));
    }
    
     @Test
    public void testIsValidPassword_InvalidPassword_TooShort() {
        assertFalse(SignUp.isValidPassword("Pass1!"));
    }
    
    
     @Test
    public void testIsValidPassword_InvalidPassword_Empty() {
        assertFalse(SignUp.isValidPassword(" "));
    }
       
    
    @Test
    public void testHashPassword() {
        String password = "Password123!";
        String hashed = SignUp.hashPassword(password);

        // Check that the hashed password is not equal to the original password
        assertNotEquals(password, hashed);

        // Check if the hashed string can be split into a salt and hashed value
        String[] parts = hashed.split(":");
        assertEquals(2, parts.length); // Ensure there are two parts (salt and hash)
        assertNotEquals("", parts[0]); // Ensure salt is not empty
        assertNotEquals("", parts[1]); // Ensure hashed password is not empty
    }
    
    
}
