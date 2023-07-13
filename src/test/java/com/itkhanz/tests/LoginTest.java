package com.itkhanz.tests;

import com.itkhanz.BaseTest;
import com.itkhanz.constants.Constants;
import com.itkhanz.pages.LoginPage;
import com.itkhanz.pages.ProductsPage;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.swing.plaf.PanelUI;
import java.io.InputStream;
import java.lang.reflect.Method;

public class LoginTest extends BaseTest {
    LoginPage loginPage;
    ProductsPage productsPage;

    InputStream loginDetails;
    JSONObject loginUsersObject;

    @BeforeClass
    public void beforeClass() {
        //TODO parse test data using Google GSON library
        //TODO use Optional in java to check for null
        try {
            loginDetails = getClass().getClassLoader().getResourceAsStream(Constants.dataFileName);
            JSONTokener tokener = new JSONTokener(loginDetails);
            loginUsersObject = new JSONObject(tokener);

            if (loginDetails != null) {
                loginDetails.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to load the test user data from " + Constants.dataFileName);
        }
    }

    @BeforeMethod
    public void beforeMethod(Method m) {
        System.out.println("\n" + "****** starting test:" + m.getName() + "******" + "\n");
        loginPage = new LoginPage();
    }

    @Test
    public void invalidUsernameTest() {
        //TODO chain the methods to achieve fluent design
        //TODO read the test data from external file
        loginPage.enterUserName(loginUsersObject.getJSONObject("invalidUser").getString("username"));
        loginPage.enterPassword(loginUsersObject.getJSONObject("invalidUser").getString("password"));
        loginPage.pressLoginBtn();

        String actualErrTxt = loginPage.getErrTxt();
        String expectedErrTxt = "Username and password do not match any user in this service.";
        System.out.println("actual error text - " + actualErrTxt + "\n" + "expected error text - " + expectedErrTxt);

        Assert.assertEquals(actualErrTxt, expectedErrTxt);
    }

    @Test
    public void invalidPasswordTest() {
        //TODO chain the methods to achieve fluent design
        //TODO read the test data from external file
        loginPage.enterUserName(loginUsersObject.getJSONObject("invalidPassword").getString("username"));
        loginPage.enterPassword(loginUsersObject.getJSONObject("invalidPassword").getString("password"));
        loginPage.pressLoginBtn();

        String actualErrTxt = loginPage.getErrTxt();
        String expectedErrTxt = "Username and password do not match any user in this service.";
        System.out.println("actual error text - " + actualErrTxt + "\n" + "expected error text - " + expectedErrTxt);

        Assert.assertEquals(actualErrTxt, expectedErrTxt);
    }

    @Test
    public void successfulLoginTest() {
        //TODO chain the methods to achieve fluent design
        //TODO read the test data from external file
        loginPage.enterUserName(loginUsersObject.getJSONObject("validUser").getString("username"));
        loginPage.enterPassword(loginUsersObject.getJSONObject("validUser").getString("password"));
        productsPage = loginPage.pressLoginBtn();

        String actualProductTitle = productsPage.getTitle();
        String expectedProductTitle = "PRODUCTS";
        System.out.println("actual product title - " + actualProductTitle + "\n" + "expected title - " + expectedProductTitle);

        Assert.assertEquals(actualProductTitle, expectedProductTitle);
    }
}